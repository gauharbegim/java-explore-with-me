package ru.practicum.ewmservice.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.category.entity.CategoryEntity;

@Component
public class CategoryMapper {
    public static CategoryDto toCategoryDto(CategoryEntity entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static CategoryEntity toNewCategoryDto(NewCategoryDto dto) {
        return CategoryEntity.builder()
                .name(dto.getName())
                .build();
    }

    public static CategoryEntity toCategoryEntity(CategoryDto dto) {
        return CategoryEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();

    }
}
