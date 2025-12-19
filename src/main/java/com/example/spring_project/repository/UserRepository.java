package com.example.spring_project.repository;

import com.example.spring_project.dto.response.UserResponse;
import com.example.spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            "((LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))) OR " +
            "(LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) OR " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :q, '%'))) OR " +
            "(LOWER(u.lastName) LIKE LOWER(CONCAT('%', :q, '%')))) AND " +
            "u.username != :currentUsername AND " +
            "NOT EXISTS (SELECT r FROM u.roles r WHERE r.name = 'ADMIN')")
    List<User> searchUsers(@Param("q") String query, @Param("currentUsername") String currentUsername);

}
