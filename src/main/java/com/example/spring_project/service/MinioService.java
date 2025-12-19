package com.example.spring_project.service;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.presign-expiry}")
    private int presignExpiry;


    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String putObject(MultipartFile file) throws Exception {
        createBucketIfNotExists();
        String objectName = UUID.randomUUID() +"-"+ file.getOriginalFilename();

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                        .object(objectName)
                        .contentType(file.getContentType())
                        .stream(file.getInputStream() ,file.getSize(),-1)

                .build());
        return objectName;
    }

    public void removeObject(String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
    }

    public String presignedUrl(String objectName) throws Exception {
//        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//                .bucket(bucket)
//                .object(objectName)
//                .method(Method.GET)
//                .expiry(presignExpiry, TimeUnit.SECONDS)
//                .build());
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(presignExpiry, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL for object: " + objectName
                    + ". Error: " + e.getMessage(), e);
        }
    }

    private void createBucketIfNotExists() throws Exception {
        if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }
}
