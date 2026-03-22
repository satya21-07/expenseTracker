package com.expensetracker.service;

import com.expensetracker.dto.CategoryDto;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    private String getUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public List<CategoryDto> getAllCategories() {
        String userId = getUserId();
        return repository.findByUserIdOrPredefinedTrue(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto createCategory(CategoryDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .color(dto.getColor())
                .predefined(false)
                .userId(getUserId())
                .build();
        return mapToDto(repository.save(category));
    }

    private CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .predefined(category.isPredefined())
                .build();
    }
}
