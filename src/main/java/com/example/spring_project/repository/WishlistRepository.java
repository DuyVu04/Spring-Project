package com.example.spring_project.repository;

import com.example.spring_project.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    @Query("SELECT w FROM Wishlist w JOIN FETCH w.course WHERE w.user.id = :userId")
    List<Wishlist> findByUserId(@Param("userId") Long userId);

    Optional<Wishlist> findByUserIdAndCourseId(Long userId, UUID courseId);

    boolean existsByUserIdAndCourseId(Long userId, UUID courseId);
}
