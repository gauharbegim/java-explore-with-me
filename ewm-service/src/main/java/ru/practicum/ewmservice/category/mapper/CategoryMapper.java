package ru.practicum.ewmservice.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.category.entity.CategoryEntity;

@Component
public class CategoryMapper {
    public static CategoryDto toCategoryDto(CategoryEntity entity) {
        CategoryDto dto = CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
        return dto;
    }

    public static CategoryEntity toNewCategoryDto(NewCategoryDto dto) {
        CategoryEntity entity = CategoryEntity.builder()
                .name(dto.getName())
                .build();
        return entity;
    }

    public static CategoryEntity toCategoryEntity(CategoryDto dto) {
        CategoryEntity entity = CategoryEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
        return entity;
    }
}
