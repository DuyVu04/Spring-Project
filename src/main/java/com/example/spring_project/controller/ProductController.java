package com.example.spring_project.controller;

import com.example.spring_project.dto.request.ProductRequest;
import com.example.spring_project.dto.request.UpdateProductRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.ProductResponse;
import com.example.spring_project.entity.Product;
import com.example.spring_project.service.ProductService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request){
        return ApiResponse.<ProductResponse>builder()
                .message("Product created successfully")
                .result(productService.createProduct(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts(){
        return ApiResponse.<List<ProductResponse>>builder()
                .message("Products retrieved successfully")
                .result(productService.getAllProducts())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable String id){
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Product deleted successfully")
                .build();
    }

}
