package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.CategoryService;
import com.fpt.ssds.service.dto.CategoryDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createUpdate(@RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(categoryService.createUpdate(categoryDto)));
    }

    @PostMapping("delete")
    public ResponseEntity<ResponseDTO> deleteById(@RequestBody List<Long> listId) {
        categoryService.deleteListCategory(listId);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(null));
    }
}
