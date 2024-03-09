package com.fpt.ssds.service;

import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.service.dto.BranchDto;

import java.util.List;
import java.util.Map;

public interface BranchService {
    Branch createUpdate(BranchDto categoryDto);

    List<BranchDto> getAll();

    String getBranchDetailedAddress(Long branchId);

    Branch findById(Long branchId);

    List<Branch> findByUserId(Long userId);
}
