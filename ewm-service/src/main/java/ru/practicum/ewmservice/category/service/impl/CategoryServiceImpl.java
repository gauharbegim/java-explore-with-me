package ru.practicum.ewmservice.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.category.entity.CategoryEntity;
import ru.practicum.ewmservice.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.category.repository.CategoryRepository;
import ru.practicum.ewmservice.category.service.CategoryService;
import ru.practicum.ewmservice.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toNewCategoryDto(newCategoryDto)));
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long catId) {
        CategoryEntity category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с таким id не найден."));

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto patch(Long catId, CategoryDto categoryDto) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с таким id не найден."));

        categoryDto.setId(catId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategoryEntity(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteById(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с таким id не не найден."));

        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryEntity getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с таким id не найден."));
    }
}
