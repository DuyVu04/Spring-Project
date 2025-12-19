package com.example.spring_project.service;

import com.example.spring_project.dto.request.CreateRoomRequest;
import com.example.spring_project.dto.response.ChatRoomResponse;
import com.example.spring_project.dto.response.ChatRoomSummaryResponse;
import com.example.spring_project.entity.ChatMessage;
import com.example.spring_project.entity.ChatRoom;
import com.example.spring_project.entity.ChatRoomMember;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.ChatRoomType;
import com.example.spring_project.enums.RoleChat;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.ChatRoomMapper;
import com.example.spring_project.repository.ChatMessageRepository;
import com.example.spring_project.repository.ChatRoomMemberRepository;
import com.example.spring_project.repository.ChatRoomRepository;
import com.example.spring_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository memberRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomMapper chatRoomMapper;

    /**
     * Create a new chat room with members
     * - PRIVATE: Auto get or create (unique room for 2 users)
     * - GROUP: Always create new room
     */
    public ChatRoomResponse createRoom(CreateRoomRequest request, String creatorUsername) {
        log.info("Creating {} chat room for user: {}", request.getType(), creatorUsername);


        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));


        if (request.getType().equals("PRIVATE")) {
            return handlePrivateRoom(creator, request);
        }


        return handleGroupRoom(creator, request);
    }

    /**
     * Handle PRIVATE room creation/getting
     */
    private ChatRoomResponse handlePrivateRoom(User creator, CreateRoomRequest request) {

        if (request.getMemberIds() == null || request.getMemberIds().size() != 1) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        Long otherUserId = request.getMemberIds().getFirst();
        log.info("Getting or creating private room between users {} and {}", creator.getId(), otherUserId);

        ChatRoom room = getOrCreatePrivateRoom(creator.getId(), otherUserId);

        List<Long> allMemberIds = new ArrayList<>();
        allMemberIds.add(creator.getId());
        allMemberIds.add(otherUserId);

        return ChatRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .type(room.getType().name())
                .memberIds(allMemberIds)
                .createdBy(creator.getId())
                .createdAt(room.getCreatedAt())
                .build();
    }

    /**
     * Handle GROUP room creation - always creates new room
     */
    private ChatRoomResponse handleGroupRoom(User creator, CreateRoomRequest request) {
        // Create chat room
        ChatRoom room = chatRoomMapper.toChatRoomFromRequest(request);
        room.setName(request.getName());
        room.setType(ChatRoomType.valueOf(request.getType()));
        room.setCreatedBy(creator.getId());
        room.setCreatedAt(Instant.now());

        chatRoomRepository.save(room);


        addMemberToRoom(room, creator, RoleChat.ADMIN);


        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            for (Long memberId : request.getMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));


                if (memberRepository.existsByChatRoomIdAndUserId(room.getId(), memberId)) {
                    log.warn("User {} is already a member of room {}", memberId, room.getId());
                    continue;
                }

                addMemberToRoom(room, member, RoleChat.MEMBER);
            }
        }

        log.info("Group room {} created successfully", room.getId());


        List<Long> allMemberIds = new ArrayList<>();
        allMemberIds.add(creator.getId());
        if (request.getMemberIds() != null) {
            allMemberIds.addAll(request.getMemberIds());
        }

        return ChatRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .type(room.getType().name())
                .memberIds(allMemberIds)
                .createdBy(room.getCreatedBy())
                .createdAt(room.getCreatedAt())
                .build();
    }

    /**
     * Get all chat rooms for a user with last message info
     */
    @Transactional(readOnly = true)
    public List<ChatRoomSummaryResponse> getUserRooms(Long userId) {
        log.info("Fetching chat rooms for user: {}", userId);

        List<ChatRoomMember> memberships = memberRepository.findByUserIdWithRoom(userId);

        return memberships.stream()
                .map(membership -> {
                    ChatRoom room = membership.getChatRoom();

                    List<ChatRoomMember> roomMembers = memberRepository.findMembersByRoomId(room.getId());

                    Optional<ChatMessage> lastMessage =
                            messageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(room.getId());

                    return toChatRoomSummaryResponse(room, roomMembers, lastMessage, userId);
                })
                .toList();
    }

    /**
     * Get or create a private room between two users
     */
    public ChatRoom getOrCreatePrivateRoom(Long userA, Long userB) {
        String roomKey = generateRoomKey(userA, userB);

        return chatRoomRepository.findByRoomKey(roomKey)
                .orElseGet(() -> {
                    log.info("Creating new private room for users {} and {}", userA, userB);

                    ChatRoom room = ChatRoom.builder()
                            .type(ChatRoomType.PRIVATE)
                            .roomKey(roomKey)
                            .createdBy(userA)
                            .createdAt(Instant.now())
                            .build();

                    chatRoomRepository.save(room);



                    User user1 = userRepository.findById(userA)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
                    User user2 = userRepository.findById(userB)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

                    addMemberToRoom(room, user1, RoleChat.MEMBER);
                    addMemberToRoom(room, user2, RoleChat.MEMBER);

                    return room;
                });
    }

    /**
     * Helper method to add member to room
     */
    private void addMemberToRoom(ChatRoom room, User user, RoleChat role) {
        if (memberRepository.existsByChatRoomIdAndUserId(room.getId(), user.getId())) {
            log.warn("User {} already exists in room {}", user.getId(), room.getId());
            return;
        }

        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(room)
                .user(user)
                .role(role)
                .joinedAt(Instant.now())
                .build();

        memberRepository.save(member);
    }

    /**
     * Generate unique room key for private rooms
     */
    private String generateRoomKey(Long a, Long b) {
        return a.compareTo(b) < 0 ? a + "_" + b : b + "_" + a;
    }

    /**
     * Map ChatRoom to ChatRoomResponse
     */
    private ChatRoomResponse toChatRoomResponse(ChatRoom room) {
        List<Long> memberIds = new ArrayList<>();
        if (room.getMembers() != null) {
            memberIds = room.getMembers().stream()
                    .map(member -> member.getUser().getId())
                    .toList();
        }

        return ChatRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .type(room.getType().name())
                .memberIds(memberIds)
                .createdBy(room.getCreatedBy())
                .createdAt(room.getCreatedAt())
                .build();
    }

    /**
     * Map to ChatRoomSummaryResponse with last message
     */
    private ChatRoomSummaryResponse toChatRoomSummaryResponse(ChatRoom room, List<ChatRoomMember> roomMembers, Optional<ChatMessage> lastMessage, Long currentUserId) {

        List<Long> memberIds = roomMembers.stream()
                .map(m -> m.getUser().getId())
                .toList();

        var response = ChatRoomSummaryResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .type(room.getType().name())
                .memberIds(memberIds)
                .build();

        if (room.getType() == ChatRoomType.PRIVATE) {
            ChatRoomMember otherMember = roomMembers.stream()
                    .filter(m -> !m.getUser().getId().equals(currentUserId))
                    .findFirst()
                    .orElse(null);

            if (otherMember != null) {
                User otherUser = otherMember.getUser();
                String displayName = (otherUser.getFirstName() != null && otherUser.getLastName() != null)
                        ? otherUser.getFirstName() + " " + otherUser.getLastName()
                        : otherUser.getUsername();

                response.setRoomName(displayName);
                response.setAvatar(otherUser.getAvatarImg());
            } else {
                response.setRoomName("Private Chat");
            }
        }

        lastMessage.ifPresent(msg -> {
            response.setLastMessage(msg.getContent());
            response.setLastMessageSenderId(msg.getSender().getId());
            response.setLastMessageTime(msg.getCreatedAt());
        });

        return response;
    }
}
