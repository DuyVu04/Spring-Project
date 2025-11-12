package com.example.spring_project.service;

import com.example.spring_project.dto.request.ProductRequest;
import com.example.spring_project.dto.response.ProductResponse;
import com.example.spring_project.entity.Product;
import com.example.spring_project.mapper.ProductMapper;
import com.example.spring_project.repository.CategoryRepository;
import com.example.spring_project.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(ProductRequest request){
        var product = productMapper.toProductFromRequest(request);
        var categories = categoryRepository.findAllById(request.getCategories());
        if (categories.isEmpty() || categories.size() != request.getCategories().size()) {
            throw new RuntimeException("One or more categories not found");
        }
        product.setCategories(new HashSet<>(categories));
        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }


    public List<ProductResponse> getAllProducts(){
        var products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(String id){
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Not found product with id: " + id);
        }
        productRepository.deleteById(id);
    }

}
