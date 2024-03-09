package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Category;
import com.fpt.ssds.service.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDto}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper extends EntityMapper<CategoryDto, Category> {
    @Mapping(target = "spaServices", ignore = true)
    Category toEntity(CategoryDto categoryDto);

    default Category fromId(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
