package com.example.spring_project.service;

import com.example.spring_project.dto.request.FriendshipRequest;
import com.example.spring_project.dto.response.FriendshipResponse;
import com.example.spring_project.entity.Friendship;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.FriendshipStatus;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.FriendshipMapper;
import com.example.spring_project.repository.FriendshipRepository;
import com.example.spring_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;

    public FriendshipResponse sendFriendRequest(FriendshipRequest request, String requesterUsername) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
        User addressee = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        if (requester.getId().equals(addressee.getId())) {
            throw new CustomException(ErrorCode.CANNOT_FRIEND_SELF);
        }

        friendshipRepository.findBetweenUsers(requester, addressee)
                .ifPresent(f -> {
                    throw new CustomException(ErrorCode.FRIEND_REQUEST_EXISTS);
                });

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .addressee(addressee)
                .status(FriendshipStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        return friendshipMapper.toFriendshipResponse(friendshipRepository.save(friendship));
    }

    public FriendshipResponse respondToRequest(Long friendshipId, String status, String username) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new CustomException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        if (!friendship.getAddressee().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        friendship.setStatus(FriendshipStatus.valueOf(status.toUpperCase()));
        return friendshipMapper.toFriendshipResponse(friendshipRepository.save(friendship));
    }

    @Transactional(readOnly = true)
    public List<FriendshipResponse> getFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return friendshipRepository.findAllFriends(user, FriendshipStatus.ACCEPTED).stream()
                .map(friendshipMapper::toFriendshipResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendshipResponse> getPendingRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return friendshipRepository.findByAddresseeAndStatus(user, FriendshipStatus.PENDING).stream()
                .map(friendshipMapper::toFriendshipResponse)
                .toList();
    }

    public void unfriend(Long friendId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        Friendship friendship = friendshipRepository.findBetweenUsers(user, friend)
                .orElseThrow(() -> new CustomException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        friendshipRepository.delete(friendship);
    }
}
