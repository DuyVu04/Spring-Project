package com.example.spring_project.repository;

import com.example.spring_project.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    // Step 1: Fetch memberships + chatRoom (safe single JOIN FETCH)
    @Query("SELECT DISTINCT m FROM ChatRoomMember m JOIN FETCH m.chatRoom WHERE m.user.id = :userId")
    List<ChatRoomMember> findByUserIdWithRoom(@Param("userId") Long userId);

    // Step 2: Fetch all members of a specific room (with user info)
    @Query("SELECT DISTINCT m FROM ChatRoomMember m JOIN FETCH m.user WHERE m.chatRoom.id = :roomId")
    List<ChatRoomMember> findMembersByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT m FROM ChatRoomMember m JOIN FETCH m.chatRoom WHERE m.chatRoom.id = :roomId")
    List<ChatRoomMember> findByRoomIdWithUser(@Param("roomId") Long roomId);

    Optional<ChatRoomMember> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}

