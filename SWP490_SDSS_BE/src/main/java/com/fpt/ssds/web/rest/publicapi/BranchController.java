package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.BranchService;
import com.fpt.ssds.service.dto.BranchDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("PublicBranchController")
@RequestMapping("${ssds.api.ref.public}/web/v1/branch")
public class BranchController {
    private final BranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAll() {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(branchService.getAll()));
    }
}
