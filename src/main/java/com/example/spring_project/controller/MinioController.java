package com.example.spring_project.controller;

import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.service.MinioService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/minio")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioController {

    MinioService minioService;

    @PostMapping("/put-object")
    public ApiResponse<String> putObject(@RequestPart("file") MultipartFile file) {
        try {
            return ApiResponse.<String>builder()
                    .result(minioService.putObject(file))
                    .message("success")
                    .build();
        } catch (Exception e){
            throw new RuntimeException("Have a error when put object" , e);
        }
    }

    @DeleteMapping("/remove")
    public ApiResponse<String> deleteObject(@RequestParam("objectName") String objectName) {
        try{
            minioService.removeObject(objectName);
            return ApiResponse.<String>builder()
                    .message("Remove file completed")
                    .build();
        } catch (Exception e){
            throw new RuntimeException("Have a error when remove object" , e);
        }
    }

    @GetMapping("/get-object-file")
    public ApiResponse<String> presign(@RequestParam("objectName") String objectName) {
        try{
            return ApiResponse.<String>builder()
                    .result(minioService.presignedUrl(objectName))
                    .message("Presigned file completed")
                    .build();
        } catch (Exception e){
            throw new RuntimeException("Have a error when presign object" , e);
        }
    }
}
