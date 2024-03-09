package com.fpt.ssds.web.rest;

import com.fpt.ssds.service.dto.BranchDto;
import com.fpt.ssds.service.BranchService;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${ssds.api.ref.internal}/web/v1/branch")
public class BranchController {
    private final BranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createUpdate(@RequestBody BranchDto branchDto) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(branchService.createUpdate(branchDto)));
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAll() {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(branchService.getAll()));
    }
}
