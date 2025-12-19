package com.example.spring_project.mapper;


import com.example.spring_project.dto.request.CategoryRequest;
import com.example.spring_project.dto.response.CategoryResponse;
import com.example.spring_project.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
    Category createCategoryFromRequest(CategoryRequest request);
    List<CategoryResponse> toListCategoryResponse(List<Category> categories);
}
