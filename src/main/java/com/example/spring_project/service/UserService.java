package com.example.spring_project.service;

import com.example.spring_project.dto.request.UserRequest;
import com.example.spring_project.dto.request.UserUpdateRequest;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.dto.response.UserResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.PageResponseMapper;
import com.example.spring_project.mapper.UserMapper;
import com.example.spring_project.repository.RoleRepository;
import com.example.spring_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserResponse getUser(String userId) {
        return userMapper.toUserResponse(
                userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOTEXISTS))
        );
    }


//    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest) {
//        var user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOTEXISTS));
//        userMapper.updateUserFromRequest(user, userUpdateRequest);
//        if (userUpdateRequest.getPassword() != null) {
//            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
//        }
//        var role =roleRepository.findAllById(userUpdateRequest.getRoles());
//        user.setRoles(new HashSet<>(role));
//        return userMapper.toUserResponse(userRepository.save(user));
//    }


    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOTEXISTS));

        // Cập nhật các field khác
        userMapper.updateUserFromRequest(user, userUpdateRequest);

        // Nếu password được truyền thì update
        if (userUpdateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        // Chỉ update role nếu request có roles
        if (userUpdateRequest.getRoles() != null && !userUpdateRequest.getRoles().isEmpty()) {
            var roles = roleRepository.findAllById(userUpdateRequest.getRoles());
            user.setRoles(new HashSet<>(roles));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }



    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOTEXISTS);
        }
        userRepository.deleteById(userId);

    }


    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name =context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new CustomException(ErrorCode.USER_NOTEXISTS));
        return userMapper.toUserResponse(user);
    }

    public PageResponse<UserResponse> getUsers(Specification<User> spec, Pageable page) {
        try{
            var userpage =
                userRepository.findAll(spec,page)
                        .map(item -> userMapper.toUserResponse(item));
            return pageResponseMapper.toPageResponse(userpage);
        }
        catch(DataIntegrityViolationException e){
            throw new CustomException(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
    }

}
