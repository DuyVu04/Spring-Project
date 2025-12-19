package com.example.spring_project.mapper;

import com.example.spring_project.dto.response.WishlistResponse;
import com.example.spring_project.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.title", target = "courseTitle")
    @Mapping(source = "course.thumbnail", target = "courseThumbnail")
    WishlistResponse toWishlistResponse(Wishlist wishlist);
}