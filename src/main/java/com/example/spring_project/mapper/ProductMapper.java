package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.ProductRequest;
import com.example.spring_project.dto.request.UpdateProductRequest;
import com.example.spring_project.dto.response.CategoryResponse;
import com.example.spring_project.dto.response.ProductResponse;
import com.example.spring_project.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "imgUrl", source = "imgUrl")
    Product toProductFromRequest(ProductRequest request);

    @Mapping(target = "imgUrl", source = "imgUrl")
    ProductResponse toProductResponse(Product product);





}
