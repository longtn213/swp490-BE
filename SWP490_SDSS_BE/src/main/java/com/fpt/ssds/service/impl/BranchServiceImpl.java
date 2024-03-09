package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.*;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.*;
import com.fpt.ssds.service.ConfigDataService;
import com.fpt.ssds.service.FileService;
import com.fpt.ssds.service.dto.BranchDto;
import com.fpt.ssds.service.BranchService;
import com.fpt.ssds.service.dto.FileDto;
import com.fpt.ssds.service.mapper.BranchMapper;
import com.fpt.ssds.service.mapper.FileMapper;
import com.fpt.ssds.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;

    private final BranchMapper branchMapper;

    private final MessageSource messageSource;

    private final LocationRepository locationRepository;

    private final ConfigDataService configDataService;

    private final FileRepository fileRepository;

    private final FileMapper fileMapper;

    private final FileService fileService;
    private final UserRepository userRepository;
    private final AppointmentMasterRepository appointmentMasterRepository;


    @Override
    @Transactional
    public Branch createUpdate(BranchDto branchDto) {
        if (Objects.nonNull(branchDto.getId())) {
            Optional<Branch> branchOpt = branchRepository.findById(branchDto.getId());
            if (branchOpt.isEmpty()) {
                throw new SSDSBusinessException(ErrorConstants.BRANCH_NOT_EXIST, Arrays.asList(branchDto.getId()));
            }
            return updateBranch(branchDto, branchOpt.get());
        } else {
            return createBranch(branchDto);
        }
    }

    @Override
    public List<BranchDto> getAll() {
        List<BranchDto> branchDtos = new ArrayList<>();

        List<Branch> branches = branchRepository.findAll();
        List<File> files = fileRepository.findByTypeAndRefIdInAndUploadStatus(FileType.BRANCH, branches.stream().map(Branch::getId).collect(Collectors.toList()), UploadStatus.SUCCESS);
        Map<Long, List<FileDto>> filesByBranchId = fileMapper.toDto(files).stream().collect(Collectors.groupingBy(FileDto::getRefId));
        for (Branch branch : branches) {
            BranchDto branchDto = branchMapper.toDto(branch);
            branchDto.setFiles(filesByBranchId.get(branch.getId()));
            branchDtos.add(branchDto);
        }

        return branchDtos;
    }

    @Override
    public String getBranchDetailedAddress(Long branchId) {
        Optional<Branch> branchOpt = branchRepository.findById(branchId);
        if (branchOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.BRANCH_NOT_EXIST);
        }
        Branch branch = branchOpt.get();
        return StringUtils.join(Arrays.asList(branch.getDetailAddress(), branch.getDistrict().getDivisionName(), branch.getCity().getDivisionName(), branch.getState().getDivisionName()), ", ");
    }

    @Override
    public Branch findById(Long branchId) {
        Optional<Branch> branchOpt = branchRepository.findById(branchId);
        if (branchOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.BRANCH_NOT_EXIST);
        }
        return branchOpt.get();
    }


    @Override
    @Transactional
    public List<Branch> findByUserId(Long userId) {
        Optional<Branch> branchOpt = branchRepository.findByUsersId(userId);
        if (branchOpt.isEmpty()) {
            return branchRepository.findAll();
        }
        return Arrays.asList(branchOpt.get());
    }

    private Branch createBranch(BranchDto branchDto) {
        Location district = locationRepository.findById(branchDto.getDistrict().getId()).get();
        String code = genBranchCode(branchDto.getName()) + "_" + district.getDivisionCode() + "_" + branchDto.getLongitude().intValue() + "_" + branchDto.getLatitude().intValue();
        branchDto.setCode(code);

        Optional<Branch> branchByCoordinateOpt = branchRepository.findByLatitudeAndLongitude(branchDto.getLatitude(), branchDto.getLongitude());
        if (branchByCoordinateOpt.isPresent()) {
            Branch branch = branchByCoordinateOpt.get();
            String message = messageSource.getMessage("branch.coordinate.already.exist", null, null);
            message = new MessageFormat(message).format(Arrays.asList(branch.getName(), getFullAddress(branch)).toArray());
            throw new SSDSBusinessException(null, message);
        }
        Branch branch = branchMapper.toEntity(branchDto);
        List<Location> locations = locationRepository.findAllById(Arrays.asList(branchDto.getState().getId(), branchDto.getCity().getId(), branchDto.getDistrict().getId()));

        saveAddressInfo(branch, branchDto, locations);
        branch.setIsActive(Boolean.TRUE);
        branch = branchRepository.save(branch);
        configDataService.createConfigForNewBranch(branch);
        if (CollectionUtils.isNotEmpty(branchDto.getFiles())) {
            if (CollectionUtils.isNotEmpty(branchDto.getFiles())) {
                fileService.updateFileRefId(branchDto.getFiles(), branch.getId());
            }
        }
        return branch;
    }

    private String getFullAddress(Branch branch) {
        List<String> addressPart = Arrays.asList(branch.getDetailAddress(), branch.getDistrict().getDivisionName(), branch.getCity().getDivisionName(), branch.getState().getDivisionName());
        String address = StringUtils.join(addressPart, ", ");
        return address;
    }

    private void saveAddressInfo(Branch branch, BranchDto branchDto, List<Location> locations) {
        Map<Long, Location> locationMap = new HashMap<>();
        for (Location location : locations) {
            locationMap.put(location.getId(), location);
        }

        branch.setState(locationMap.get(branchDto.getState().getId()));
        branch.setCity(locationMap.get(branchDto.getCity().getId()));
        branch.setDistrict(locationMap.get(branchDto.getDistrict().getId()));

    }

    private Branch updateBranch(BranchDto branchDto, Branch branch) {
        if (!branchDto.getIsActive() && branch.getIsActive()) {
            Integer totalNotStartAm = appointmentMasterRepository.countByBranchIdAndStatusCodeIn(branch.getId(), Arrays.asList(Constants.APPOINTMENT_MASTER_STATUS.WAITING_FOR_CONFIRMATION, Constants.APPOINTMENT_MASTER_STATUS.READY));
            if (totalNotStartAm > 0) {
                throw new SSDSBusinessException(null, messageSource.getMessage("branch.deactive.have.not.been.started.am", null, null));
            }
        }
        String code = branchDto.getCode();
        if (Objects.nonNull(code)) {
            Optional<Branch> branchOpt = branchRepository.findByCode(code);
            if (branchOpt.isPresent()) {
                if (!branchOpt.get().getId().equals(branchDto.getId())) {
                    String message = messageSource.getMessage("branch.code.already.exist", null, null);
                    message = new MessageFormat(message).format(Arrays.asList(code).toArray());
                    throw new SSDSBusinessException(null, message);
                }
            }
        }

        Optional<Branch> branchByCoordinateOpt = branchRepository.findByLatitudeAndLongitude(branchDto.getLatitude(), branchDto.getLongitude());
        if (branchByCoordinateOpt.isPresent()) {
            Branch branchByCoordinate = branchByCoordinateOpt.get();
            if (!branchByCoordinate.getId().equals(branchDto.getId())) {
                String message = messageSource.getMessage("branch.coordinate.already.exist", null, null);
                message = new MessageFormat(message).format(Arrays.asList(branchByCoordinate.getName(), getFullAddress(branchByCoordinate)).toArray());
                throw new SSDSBusinessException(null, message);
            }
        }

        List<Location> locations = locationRepository.findAllById(Arrays.asList(branchDto.getDistrict().getId(), branchDto.getCity().getId(), branchDto.getState().getId()));
        saveAddressInfo(branch, branchDto, locations);

        if (CollectionUtils.isNotEmpty(branchDto.getFiles())) {
            fileService.updateFileRefId(branchDto.getFiles(), branch.getId());
        }
        return branchRepository.save(branchMapper.toEntity(branchDto));
    }

    private String genBranchCode(String branchName) {
        if (Objects.isNull(branchName)) {
            throw new SSDSBusinessException(ErrorConstants.BRANCH_NAME_IS_REQUIRED);
        }
        return Utils.deAccent(branchName).toUpperCase().replace(" ", "_");
    }
}
