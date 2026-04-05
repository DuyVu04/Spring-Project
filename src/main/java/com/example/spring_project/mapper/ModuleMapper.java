package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.ModuleRequest;
import com.example.spring_project.dto.response.ModuleResponse;
import com.example.spring_project.entity.Module;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

    Module toModule(ModuleRequest request);

    ModuleResponse toModuleResponse(Module module);
}