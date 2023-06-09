package ru.practicum.ewmservice.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.category.entity.CategoryEntity;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(Long catId);

    CategoryDto patch(Long catId, CategoryDto categoryDto);

    void deleteById(Long catId);

    CategoryEntity getCategoryById(Long catId);
}
