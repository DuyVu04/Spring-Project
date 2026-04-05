package com.example.spring_project.controller;

import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatMediaController {

    private final MinioService minioService;

    /**
     * Upload media (image/audio/video) for chat messages
     *
     * Client uploads file via REST API → gets URL back → sends URL via WebSocket in mediaUrl field
     *
     * @param file The media file to upload
     * @return ApiResponse containing the file URL
     */
    @PostMapping(value = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadMedia(
            @RequestParam("file") MultipartFile file
    ) {
        log.info("Uploading chat media file: {}", file.getOriginalFilename());

        try {

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.debug("Media upload by user: {}", username);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + extension;


            String objectName = minioService.putObject(file);

            log.info("Media uploaded successfully. ObjectName: {}", objectName);


            Map<String, String> result = new HashMap<>();
            result.put("url", objectName);
            result.put("filename", originalFilename != null ? originalFilename : filename);
            result.put("contentType", file.getContentType());

            return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                    .code(1000)
                    .message("File uploaded successfully")
                    .result(result)
                    .build());

        } catch (Exception e) {
            log.error("Failed to upload media: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, String>>builder()
                            .code(5000)
                            .message("Failed to upload file: " + e.getMessage())
                            .build()
            );
        }
    }
}
