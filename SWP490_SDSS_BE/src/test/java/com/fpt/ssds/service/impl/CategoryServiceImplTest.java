package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.domain.Category;
import com.fpt.ssds.repository.CategoryRepository;
import com.fpt.ssds.service.dto.CategoryDto;
import com.fpt.ssds.service.mapper.CategoryMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;


@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Spy
    private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    public void when_dataValid_should_createSuccessful() {
        CategoryDto categoryDto = initCategoryDto();
        Mockito.when(categoryRepository.findByCode("CATEGORY")).thenReturn(Optional.ofNullable(null));
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(initCategory());

        categoryService.createUpdate(categoryDto);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void when_categoryNotExist_should_updateCategoryThrowException() {
        CategoryDto categoryDto = initCategoryDto();
        categoryDto.setId(1L);
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> categoryService.createUpdate(categoryDto));
        Assert.assertEquals("CATEGORY_NOT_EXIST", ssdsBusinessException.getCode());
    }

    @Test
    public void when_codeDuplicate_should_createThrowException() {
        CategoryDto categoryDto = initCategoryDto();
        Mockito.when(categoryRepository.findByCode(Mockito.anyString())).thenReturn(Optional.of(new Category()));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> categoryService.createUpdate(categoryDto));
        Assert.assertEquals("CATEGORY_ALREADY_EXIST", ssdsBusinessException.getCode());
    }

    @Test
    public void when_codeDuplicate_should_updateThrowException() {
        CategoryDto categoryDto = initCategoryDto();
        categoryDto.setId(1L);

        Category category = initCategory();
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        category.setId(2L);
        Mockito.when(categoryRepository.findByCode(Mockito.anyString())).thenReturn(Optional.of(category));
        SSDSBusinessException ssdsBusinessException = Assert.assertThrows(SSDSBusinessException.class, () -> categoryService.createUpdate(categoryDto));
        Assert.assertEquals("CATEGORY_ALREADY_EXIST", ssdsBusinessException.getCode());
    }

    private CategoryDto initCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("category");
        return categoryDto;
    }

    private Category initCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("category");
        return category;
    }
}
