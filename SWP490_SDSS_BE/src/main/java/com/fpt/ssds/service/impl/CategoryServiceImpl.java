package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.Category;
import com.fpt.ssds.domain.SpaService;
import com.fpt.ssds.repository.CategoryRepository;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.CategoryService;
import com.fpt.ssds.service.dto.CategoryDto;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.mapper.CategoryMapper;
import com.fpt.ssds.utils.Utils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    private final SpaServiceRepository spaServiceRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper, SpaServiceRepository spaServiceRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.spaServiceRepository = spaServiceRepository;
    }

    @Override
    public Category createUpdate(CategoryDto categoryDto) {
        Category category = new Category();
        if (Objects.isNull(categoryDto.getCode())) {
            categoryDto.setCode(Utils.genCodeFromName(categoryDto.getName()));
        }
        if (Objects.nonNull(categoryDto.getId())) {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryDto.getId());
            if (categoryOpt.isEmpty()) {
                throw new SSDSBusinessException(ErrorConstants.CATEGORY_NOT_EXIST);
            }
            category = updateCategory(categoryDto);
        } else {
            category = createCategory(categoryDto);
        }
        addServiceToCategory(category, categoryDto);
        return category;
    }

    @Override
    @Transactional
    public List<CategoryDto> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDto(categories);
    }

    @Override
    @Transactional
    public CategoryDto findById(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.CATEGORY_NOT_EXIST, Arrays.asList(id));
        }
        return categoryMapper.toDto(categoryOpt.get());
    }

    @Override
    @Transactional
    public void deleteListCategory(List<Long> listId) {
        if (!CollectionUtils.isEmpty(listId)) {
            List<SpaService> services = spaServiceRepository.findByCategoryIdIn(listId);
            services.forEach(spaService -> spaService.setCategory(null));
            spaServiceRepository.saveAll(services);
            List<Category> categories = categoryRepository.findAllById(listId);
            List<Long> listExistId = categories.stream().map(Category::getId).collect(Collectors.toList());
            List<Long> invalidId = new ArrayList<>(listId);
            invalidId.removeAll(listExistId);
            if (CollectionUtils.isNotEmpty(invalidId)) {
                throw new SSDSBusinessException("Các danh mục với id " + StringUtils.join(invalidId, ", ") + " không tồn tại. Vui lòng kiểm tra và thử lại.");
            }
            categoryRepository.deleteAllById(listExistId);
        }
    }

    private Category createCategory(CategoryDto categoryDto) {
        String code = categoryDto.getCode();
        Optional<Category> categoryOpt = categoryRepository.findByCode(code);
        if (categoryOpt.isPresent()) {
            throw new SSDSBusinessException(ErrorConstants.CATEGORY_ALREADY_EXIST, Arrays.asList(code));
        }
        Category category = categoryRepository.save(categoryMapper.toEntity(categoryDto));
        categoryDto.setId(category.getId());
        return category;
    }

    private Category updateCategory(CategoryDto categoryDto) {
        Optional<Category> categoryOpt = categoryRepository.findByCode(categoryDto.getCode());
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            if (!category.getId().equals(categoryDto.getId())) {
                throw new SSDSBusinessException(ErrorConstants.CATEGORY_ALREADY_EXIST, Arrays.asList(categoryDto.getName()));
            }
        }
        List<SpaService> spaServices = spaServiceRepository.findByCategoryIdIn(Arrays.asList(categoryDto.getId()));
        if (CollectionUtils.isNotEmpty(spaServices)) {
            for (SpaService spaService : spaServices) {
                spaService.setCategory(null);
            }
            spaServiceRepository.saveAll(spaServices);
        }

        return categoryRepository.save(categoryMapper.toEntity(categoryDto));
    }

    private void addServiceToCategory(Category category, CategoryDto categoryDto) {
        List<SpaServiceDto> spaServiceDtos = categoryDto.getSpaServices();
        if (CollectionUtils.isNotEmpty(spaServiceDtos)) {
            List<Long> listServiceId = spaServiceDtos.stream()
                .filter(serviceDto -> Objects.nonNull(serviceDto.getId()))
                .map(SpaServiceDto::getId)
                .collect(Collectors.toList());
            List<SpaService> services = spaServiceRepository.findByIdIn(listServiceId);
            services.forEach(spaService -> spaService.setCategory(category));
            spaServiceRepository.saveAll(services);
        }
    }
}
