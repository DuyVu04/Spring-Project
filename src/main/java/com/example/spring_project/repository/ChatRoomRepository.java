package com.example.spring_project.repository;

import com.example.spring_project.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomKey(String roomKey);

    @Query("SELECT DISTINCT cr FROM ChatRoom cr LEFT JOIN FETCH cr.members WHERE cr.id = :id")
    Optional<ChatRoom> findByIdWithMembers(@Param("id") Long id);

    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN FETCH cr.messages WHERE cr.id IN :ids")
    List<ChatRoom> findAllByIdsWithMessages(@Param("ids") List<Long> ids);
}
