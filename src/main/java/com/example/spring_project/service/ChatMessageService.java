package com.example.spring_project.service;

import com.example.spring_project.dto.request.ChatMessageRequest;
import com.example.spring_project.dto.response.ChatMessageResponse;
import com.example.spring_project.dto.response.ChatMessageWSResponse;
import com.example.spring_project.entity.ChatMessage;
import com.example.spring_project.entity.ChatRoom;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.MessageType;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.ChatMessageMapper;
import com.example.spring_project.repository.ChatMessageRepository;
import com.example.spring_project.repository.ChatRoomRepository;
import com.example.spring_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {
    
    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;

    /**
     * Send a message to a chat room
     * TODO Phase 3: This will be called from WebSocket controller
     */
    public ChatMessageWSResponse sendMessage(ChatMessageRequest request, String senderUsername) {
        log.info("Sending message to room {} from user {}", request.getRoomId(), senderUsername);

        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(request.getContent())
                .type(MessageType.valueOf(request.getMessageType()))
                .mediaUrl(request.getMediaUrl())
                .build();
        
        messageRepository.save(message);
        
        log.info("Message {} saved successfully", message.getId());
        
        return chatMessageMapper.toChatMessageWSResponse(message);
    }

    /**
     * Get messages for a room with pagination
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        log.info("Fetching messages for room {}, page: {}, size: {}", roomId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        
        return messageRepository
                .findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .map(chatMessageMapper::toChatMessageResponse);
    }
}
