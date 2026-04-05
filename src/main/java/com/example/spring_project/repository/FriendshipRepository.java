package com.example.spring_project.repository;

import com.example.spring_project.entity.Friendship;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findByRequesterAndAddressee(User requester, User addressee);

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :u1 AND f.addressee = :u2) OR (f.requester = :u2 AND f.addressee = :u1)")
    Optional<Friendship> findBetweenUsers(@Param("u1") User u1, @Param("u2") User u2);

    List<Friendship> findByAddresseeAndStatus(User addressee, FriendshipStatus status);

    List<Friendship> findByRequesterAndStatus(User requester, FriendshipStatus status);

    @Query("SELECT DISTINCT f FROM Friendship f " +
            "JOIN FETCH f.requester " +
            "JOIN FETCH f.addressee " +
            "WHERE (f.requester = :user OR f.addressee = :user) AND f.status = :status")
    List<Friendship> findAllFriends(@Param("user") User user, @Param("status") FriendshipStatus status);
}
