package com.fpt.ssds.service;

import com.fpt.ssds.domain.Category;
import com.fpt.ssds.service.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    Category createUpdate(CategoryDto categoryDto);

    List<CategoryDto> getAll();

    CategoryDto findById(Long id);

    void deleteListCategory(List<Long> lstId);
}
