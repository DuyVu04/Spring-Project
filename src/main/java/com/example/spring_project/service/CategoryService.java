package com.example.spring_project.service;

import com.example.spring_project.dto.request.CategoryRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.CategoryResponse;
import com.example.spring_project.entity.Category;
import com.example.spring_project.mapper.CategoryMapper;
import com.example.spring_project.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.findByName(request.getName()).isPresent()){
            throw new RuntimeException("Category already exists");
        }
        var category= categoryMapper.createCategoryFromRequest(request);
        categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryResponse> getAllCategories(){
        var categories = categoryRepository.findAll();
        return categoryMapper.toListCategoryResponse(categories);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(String categoryId){
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }

}
