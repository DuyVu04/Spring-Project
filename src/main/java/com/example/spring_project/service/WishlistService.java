package com.example.spring_project.service;

import com.example.spring_project.dto.response.WishlistResponse;
import com.example.spring_project.entity.Wishlist;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.WishlistMapper;
import com.example.spring_project.repository.CourseRepository;
import com.example.spring_project.repository.UserRepository;
import com.example.spring_project.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final WishlistMapper wishlistMapper;

    @CacheEvict(value = "wishlist", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public WishlistResponse addToWishlist(UUID courseId) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        if (wishlistRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new CustomException(ErrorCode.WISHLIST_ALREADY_EXISTS);
        }

        var wishlist = Wishlist.builder()
                .user(user)
                .course(course)
                .build();

        wishlistRepository.save(wishlist);
        return wishlistMapper.toWishlistResponse(wishlist);
    }

    @CacheEvict(value = "wishlist", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void removeFromWishlist(UUID courseId) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        var wishlist = wishlistRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.WISHLIST_NOT_FOUND));

        wishlistRepository.delete(wishlist);
    }

    @Cacheable(value = "wishlist", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()", unless = "#result == null")
    public List<WishlistResponse> getMyWishlist() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return wishlistRepository.findByUserId(user.getId()).stream()
                .map(wishlistMapper::toWishlistResponse)
                .collect(Collectors.toList());
    }
}
