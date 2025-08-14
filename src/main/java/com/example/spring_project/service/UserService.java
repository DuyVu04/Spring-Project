package com.example.spring_project.service;

import com.example.spring_project.dto.request.UserRequest;
import com.example.spring_project.dto.request.UserUpdateRequest;
import com.example.spring_project.dto.response.UserResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.Role;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.UserMapper;
import com.example.spring_project.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Sử dụng @RequiredArgsConstructor để tự động tạo constructor với các trường final thay cho @Autowired /n private UserRepository userRepository;
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true) //thay cho cai nay private final UserRepository userRepository;
public class UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;
    public User createUser(UserRequest userRequest) {
        if(userRepository.existsByUsername(userRequest.getUsername())) {
            throw new CustomException(ErrorCode.USER_EXISTS);
        }

//        User user = new User();
//        user.setUsername(userRequest.getUsername());
//        user.setPassword(userRequest.getPassword());
//        user.setFirstName(userRequest.getFirstName());
//        user.setLastName(userRequest.getLastName());
//        user.setDob(userRequest.getDob());


        //Use mapper to convert UserRequest to User entity cần Mapstruct
        User user = userMapper.toUser(userRequest);

        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu dung PasswordEncoder
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));//  mã hóa mật khẩu userRequest trước khi lưu vào cơ sở dữ liệu


        //set role mac dinh
        HashSet<String> roles =new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);


        return userRepository.save(user);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        log.info("In method get all users");
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = userMapper.toListUserResponse(users);
        return userResponses;
    }



    public User getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }



    public User updateUser(String userId, UserUpdateRequest userUpdateRequest) {
        User user = getUser(userId);
//        user.setPassword(userUpdateRequest.getPassword());
//        user.setFirstName(userUpdateRequest.getFirstName());
//        user.setLastName(userUpdateRequest.getLastName());
//        user.setDob(userUpdateRequest.getDob());

        //Use mapper to update User entity cần Mapstruct
        userMapper.updateUserFromRequest(user, userUpdateRequest);

        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }


    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name =context.getAuthentication().getName();


        User user = userRepository.findByUsername(name).orElseThrow(()-> new CustomException(ErrorCode.USER_NOTEXISTS));
        return userMapper.toUserResponse(user);
    }

}
