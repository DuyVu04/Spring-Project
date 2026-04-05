package com.example.spring_project.controller;

import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send-email")
    public ApiResponse<String> sendEmail(@RequestParam String recipients, @RequestParam String subject, @RequestParam String content , @RequestParam(required = false) MultipartFile[] files) {
        try{
            return ApiResponse.<String>builder()
                    .result(mailService.sendEmail(recipients,subject,content,files))
                    .build();
        } catch (Exception e){
            log.error("Error when send email, errorMessage={}", e.getMessage());
            return ApiResponse.<String>builder()
                    .message("Error when send email")
                    .build();
        }
    }
}
