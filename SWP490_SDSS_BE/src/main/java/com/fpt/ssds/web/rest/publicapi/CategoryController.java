package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.CategoryService;
import com.fpt.ssds.service.dto.CategoryDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("PublicCategoryController")
@RequestMapping("${ssds.api.ref.public}/web/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAll() {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(categoryService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(categoryService.findById(id)));
    }
}
