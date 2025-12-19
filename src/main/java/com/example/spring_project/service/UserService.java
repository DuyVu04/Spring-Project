package com.example.spring_project.service;

import com.example.spring_project.dto.request.UpdateFromUserRequest;
import com.example.spring_project.dto.request.UserRequest;
import com.example.spring_project.dto.request.UserUpdateRequest;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.dto.response.UserResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.ActionType;
import com.example.spring_project.enums.MembershipType;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.PageResponseMapper;
import com.example.spring_project.mapper.UserMapper;
import com.example.spring_project.repository.RoleRepository;
import com.example.spring_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final ActivityLogService activityLogService;
    private final PasswordEncoder passwordEncoder;
    private final PageResponseMapper pageResponseMapper;

    public UserResponse createUser(UserRequest userRequest) {
        if(userRepository.existsByUsername(userRequest.getUsername())) {
            throw new CustomException(ErrorCode.USER_EXISTS);
        }
        var user = userMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        var role= roleRepository.findByNameIn(List.of("USER"));
        user.setRoles(role.stream().collect(HashSet::new, Set::add, Set::addAll));
        user.setMembershipType(MembershipType.FREE);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(Long userId) {
        return userMapper.toUserResponse(
                userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS))
        );
    }

    @Cacheable(value = "users", key = "#username", unless = "#result == null")
    public UserResponse getUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(user);
    }

    @CacheEvict(value = "users", key = "#result.username")
    public UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        userMapper.updateUserFromRequest(user, userUpdateRequest);

        if (userUpdateRequest.getEmail() != null) {
            user.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        if (userUpdateRequest.getMembershipType() != null) {
            user.setMembershipType(userUpdateRequest.getMembershipType());
        }

        if (userUpdateRequest.getRoles() != null && !userUpdateRequest.getRoles().isEmpty()) {
            var roles = roleRepository.findAllById(userUpdateRequest.getRoles());
            user.setRoles(new HashSet<>(roles));
        }

        activityLogService.logActivity(user, ActionType.PROFILE_UPDATE, "Admin updated user profile: " + user.getUsername());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_EXISTS);
        }
        userRepository.deleteById(userId);
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return getUserByUsername(name);
    }

    @CacheEvict(value = "users", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public UserResponse updateInformationFromUser(UpdateFromUserRequest userUpdateRequest) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        var user = userRepository.findByUsername(name).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        userMapper.userUpdateInfomation(user, userUpdateRequest);

        if (userUpdateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        activityLogService.logActivity(user, ActionType.PROFILE_UPDATE, "User updated their own profile information");

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public PageResponse<UserResponse> getUsers(Specification<User> spec, Pageable page) {
        try {
            var userpage = userRepository.findAll(spec, page)
                    .map(item -> userMapper.toUserResponse(item));
            return pageResponseMapper.toPageResponse(userpage);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
    }

    public List<UserResponse> searchUsers(String query) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.searchUsers(query, currentUsername).stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
}
