//package com.fpt.ssds.service.impl;
//
//import com.fpt.ssds.domain.*;
//import com.fpt.ssds.repository.*;
//import com.fpt.ssds.service.dto.ResponseDTO;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class CommonServiceImplTest {
//
//    @Mock
//    private CategoryRepository mockCategoryRepository;
//    @Mock
//    private EquipmentTypeRepository mockEquipmentTypeRepository;
//    @Mock
//    private LookupRepository mockLookupRepository;
//    @Mock
//    private ReasonMessageRepository mockReasonMessageRepository;
//    @Mock
//    private BranchRepository mockBranchRepository;
//
//    @InjectMocks
//    private CommonServiceImpl commonServiceImplUnderTest;
//
//    @Test
//    public void testGetSelectionByType() {
//        // Setup
//        // Configure CategoryRepository.findAll(...).
//        final Category category = new Category();
//        category.setId(0L);
//        category.setName("name");
//        category.setCode("code");
//        category.setDescription("description");
//        final SpaService spaService = new SpaService();
//        spaService.setId(0L);
//        spaService.setName("name");
//        spaService.setCode("code");
//        spaService.setDescription("description");
//        spaService.setDuration(0L);
//        spaService.setIsActive(false);
//        spaService.setCurrentPrice(0.0);
//        spaService.setBookedCount(0L);
//        final EquipmentType equipmentType = new EquipmentType();
//        equipmentType.setId(0L);
//        spaService.setEquipmentType(equipmentType);
//        category.setSpaServices(List.of(spaService));
//        final List<Category> categories = List.of(category);
//        when(mockCategoryRepository.findAll()).thenReturn(categories);
//
//        // Configure EquipmentTypeRepository.findAll(...).
//        final EquipmentType equipmentType1 = new EquipmentType();
//        equipmentType1.setId(0L);
//        equipmentType1.setName("name");
//        equipmentType1.setCode("code");
//        equipmentType1.setDescription("description");
//        final EquipmentBranch equipmentBranch = new EquipmentBranch();
//        equipmentBranch.setId(0L);
//        final Branch branch = new Branch();
//        branch.setId(0L);
//        branch.setName("name");
//        branch.setCode("code");
//        branch.setDetailAddress("detailAddress");
//        branch.setCountry("country");
//        branch.setLatitude(0.0);
//        branch.setLongitude(0.0);
//        branch.setHotline("hotline");
//        equipmentBranch.setBranch(branch);
//        equipmentType1.setEquipmentBranches(Set.of(equipmentBranch));
//        final List<EquipmentType> equipmentTypes = List.of(equipmentType1);
//        when(mockEquipmentTypeRepository.findAll()).thenReturn(equipmentTypes);
//
//        // Configure LookupRepository.findByLookupKey(...).
//        final List<Lookup> lookups = List.of(new Lookup(0L, "lookupKey", "code", "name", "description"));
//        when(mockLookupRepository.findByLookupKey("APPOINTMENT_MASTER_STATUS")).thenReturn(lookups);
//
//        // Configure ReasonMessageRepository.findAll(...).
//        final List<ReasonMessage> reasonMessages = List.of(
//            new ReasonMessage(0L, "name", "code", "description", false, false,
//                new ReasonMessageType(0L, "name", "code", "description")));
//        when(mockReasonMessageRepository.findAll()).thenReturn(reasonMessages);
//
//        // Configure BranchRepository.findAllByIsActive(...).
//        final Branch branch1 = new Branch();
//        branch1.setId(0L);
//        branch1.setName("name");
//        branch1.setCode("code");
//        branch1.setDetailAddress("detailAddress");
//        branch1.setCountry("country");
//        branch1.setLatitude(0.0);
//        branch1.setLongitude(0.0);
//        branch1.setHotline("hotline");
//        branch1.setIsActive(false);
//        final ConfigData configData = new ConfigData();
//        configData.setId(0L);
//        configData.setConfigKey("configKey");
//        configData.setConfigValue("configValue");
//        configData.setConfigDesc("configDesc");
//        configData.setAllowUpdate(false);
//        branch1.setConfigDataSet(Set.of(configData));
//        final List<Branch> branchList = List.of(branch1);
//        when(mockBranchRepository.findAllByIsActive(Boolean.TRUE)).thenReturn(branchList);
//
//        // Run the test
//        final ResponseDTO result = commonServiceImplUnderTest.getSelectionByType("type");
//
//        // Verify the results
//    }
//
//    @Test
//    public void testGetSelectionByType_CategoryRepositoryReturnsNoItems() {
//        // Setup
//        when(mockCategoryRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Configure EquipmentTypeRepository.findAll(...).
//        final EquipmentType equipmentType = new EquipmentType();
//        equipmentType.setId(0L);
//        equipmentType.setName("name");
//        equipmentType.setCode("code");
//        equipmentType.setDescription("description");
//        final EquipmentBranch equipmentBranch = new EquipmentBranch();
//        equipmentBranch.setId(0L);
//        final Branch branch = new Branch();
//        branch.setId(0L);
//        branch.setName("name");
//        branch.setCode("code");
//        branch.setDetailAddress("detailAddress");
//        branch.setCountry("country");
//        branch.setLatitude(0.0);
//        branch.setLongitude(0.0);
//        branch.setHotline("hotline");
//        equipmentBranch.setBranch(branch);
//        equipmentType.setEquipmentBranches(Set.of(equipmentBranch));
//        final List<EquipmentType> equipmentTypes = List.of(equipmentType);
//        when(mockEquipmentTypeRepository.findAll()).thenReturn(equipmentTypes);
//
//        // Configure LookupRepository.findByLookupKey(...).
//        final List<Lookup> lookups = List.of(new Lookup(0L, "lookupKey", "code", "name", "description"));
//        when(mockLookupRepository.findByLookupKey("APPOINTMENT_MASTER_STATUS")).thenReturn(lookups);
//
//        // Configure ReasonMessageRepository.findAll(...).
//        final List<ReasonMessage> reasonMessages = List.of(
//            new ReasonMessage(0L, "name", "code", "description", false, false,
//                new ReasonMessageType(0L, "name", "code", "description")));
//        when(mockReasonMessageRepository.findAll()).thenReturn(reasonMessages);
//
//        // Configure BranchRepository.findAllByIsActive(...).
//        final Branch branch1 = new Branch();
//        branch1.setId(0L);
//        branch1.setName("name");
//        branch1.setCode("code");
//        branch1.setDetailAddress("detailAddress");
//        branch1.setCountry("country");
//        branch1.setLatitude(0.0);
//        branch1.setLongitude(0.0);
//        branch1.setHotline("hotline");
//        branch1.setIsActive(false);
//        final ConfigData configData = new ConfigData();
//        configData.setId(0L);
//        configData.setConfigKey("configKey");
//        configData.setConfigValue("configValue");
//        configData.setConfigDesc("configDesc");
//        configData.setAllowUpdate(false);
//        branch1.setConfigDataSet(Set.of(configData));
//        final List<Branch> branchList = List.of(branch1);
//        when(mockBranchRepository.findAllByIsActive(Boolean.TRUE)).thenReturn(branchList);
//
//        // Run the test
//        final ResponseDTO result = commonServiceImplUnderTest.getSelectionByType("type");
//
//        // Verify the results
//    }
//
//    @Test
//    public void testGetSelectionByType_EquipmentTypeRepositoryReturnsNoItems() {
//        // Setup
//        // Configure CategoryRepository.findAll(...).
//        final Category category = new Category();
//        category.setId(0L);
//        category.setName("name");
//        category.setCode("code");
//        category.setDescription("description");
//        final SpaService spaService = new SpaService();
//        spaService.setId(0L);
//        spaService.setName("name");
//        spaService.setCode("code");
//        spaService.setDescription("description");
//        spaService.setDuration(0L);
//        spaService.setIsActive(false);
//        spaService.setCurrentPrice(0.0);
//        spaService.setBookedCount(0L);
//        final EquipmentType equipmentType = new EquipmentType();
//        equipmentType.setId(0L);
//        spaService.setEquipmentType(equipmentType);
//        category.setSpaServices(List.of(spaService));
//        final List<Category> categories = List.of(category);
//        when(mockCategoryRepository.findAll()).thenReturn(categories);
//
//        when(mockEquipmentTypeRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Configure LookupRepository.findByLookupKey(...).
//        final List<Lookup> lookups = List.of(new Lookup(0L, "lookupKey", "code", "name", "description"));
//        when(mockLookupRepository.findByLookupKey("APPOINTMENT_MASTER_STATUS")).thenReturn(lookups);
//
//        // Configure ReasonMessageRepository.findAll(...).
//        final List<ReasonMessage> reasonMessages = List.of(
//            new ReasonMessage(0L, "name", "code", "description", false, false,
//                new ReasonMessageType(0L, "name", "code", "description")));
//        when(mockReasonMessageRepository.findAll()).thenReturn(reasonMessages);
//
//        // Configure BranchRepository.findAllByIsActive(...).
//        final Branch branch = new Branch();
//        branch.setId(0L);
//        branch.setName("name");
//        branch.setCode("code");
//        branch.setDetailAddress("detailAddress");
//        branch.setCountry("country");
//        branch.setLatitude(0.0);
//        branch.setLongitude(0.0);
//        branch.setHotline("hotline");
//        branch.setIsActive(false);
//        final ConfigData configData = new ConfigData();
//        configData.setId(0L);
//        configData.setConfigKey("configKey");
//        configData.setConfigValue("configValue");
//        configData.setConfigDesc("configDesc");
//        configData.setAllowUpdate(false);
//        branch.setConfigDataSet(Set.of(configData));
//        final List<Branch> branchList = List.of(branch);
//        when(mockBranchRepository.findAllByIsActive(Boolean.TRUE)).thenReturn(branchList);
//
//        // Run the test
//        final ResponseDTO result = commonServiceImplUnderTest.getSelectionByType("type");
//
//        // Verify the results
//    }
//
//    @Test
//    public void testGetSelectionByType_LookupRepositoryReturnsNoItems() {
//        // Setup
//        // Configure CategoryRepository.findAll(...).
//        final Category category = new Category();
//        category.setId(0L);
//        category.setName("name");
//        category.setCode("code");
//        category.setDescription("description");
//        final SpaService spaService = new SpaService();
//        spaService.setId(0L);
//        spaService.setName("name");
//        spaService.setCode("code");
//        spaService.setDescription("description");
//        spaService.setDuration(0L);
//        spaService.setIsActive(false);
//        spaService.setCurrentPrice(0.0);
//        spaService.setBookedCount(0L);
//        final EquipmentType equipmentType = new EquipmentType();
//        equipmentType.setId(0L);
//        spaService.setEquipmentType(equipmentType);
//        category.setSpaServices(List.of(spaService));
//        final List<Category> categories = List.of(category);
//        when(mockCategoryRepository.findAll()).thenReturn(categories);
//
//        // Configure EquipmentTypeRepository.findAll(...).
//        final EquipmentType equipmentType1 = new EquipmentType();
//        equipmentType1.setId(0L);
//        equipmentType1.setName("name");
//        equipmentType1.setCode("code");
//        equipmentType1.setDescription("description");
//        final EquipmentBranch equipmentBranch = new EquipmentBranch();
//        equipmentBranch.setId(0L);
//        final Branch branch = new Branch();
//        branch.setId(0L);
//        branch.setName("name");
//        branch.setCode("code");
//        branch.setDetailAddress("detailAddress");
//        branch.setCountry("country");
//        branch.setLatitude(0.0);
//        branch.setLongitude(0.0);
//        branch.setHotline("hotline");
//        equipmentBranch.setBranch(branch);
//        equipmentType1.setEquipmentBranches(Set.of(equipmentBranch));
//        final List<EquipmentType> equipmentTypes = List.of(equipmentType1);
//        when(mockEquipmentTypeRepository.findAll()).thenReturn(equipmentTypes);
//
//        when(mockLookupRepository.findByLookupKey("APPOINTMENT_MASTER_STATUS")).thenReturn(Collections.emptyList());
//
//        // Configure ReasonMessageRepository.findAll(...).
//        final List<ReasonMessage> reasonMessages = List.of(
//            new ReasonMessage(0L, "name", "code", "description", false, false,
//                new ReasonMessageType(0L, "name", "code", "description")));
//        when(mockReasonMessageRepository.findAll()).thenReturn(reasonMessages);
//
//        // Configure BranchRepository.findAllByIsActive(...).
//        final Branch branch1 = new Branch();
//        branch1.setId(0L);
//        branch1.setName("name");
//        branch1.setCode("code");
//        branch1.setDetailAddress("detailAddress");
//        branch1.setCountry("country");
//        branch1.setLatitude(0.0);
//        branch1.setLongitude(0.0);
//        branch1.setHotline("hotline");
//        branch1.setIsActive(false);
//        final ConfigData configData = new ConfigData();
//        configData.setId(0L);
//        configData.setConfigKey("configKey");
//        configData.setConfigValue("configValue");
//        configData.setConfigDesc("configDesc");
//        configData.setAllowUpdate(false);
//        branch1.setConfigDataSet(Set.of(configData));
//        final List<Branch> branchList = List.of(branch1);
//        when(mockBranchRepository.findAllByIsActive(Boolean.TRUE)).thenReturn(branchList);
//
//        // Run the test
//        final ResponseDTO result = commonServiceImplUnderTest.getSelectionByType("type");
//
//        // Verify the results
//    }
//
//    @Test
//    public void testGetSelectionByType_ReasonMessageRepositoryReturnsNoItems() {
//        // Setup
//        // Configure CategoryRepository.findAll(...).
//        final Category category = new Category();
//        category.setId(0L);
//        category.setName("name");
//        category.setCode("code");
//        category.setDescription("description");
//        final SpaService spaService = new SpaService();
//        spaService.setId(0L);
//        spaService.setName("name");
//        spaService.setCode("code");
//        spaService.setDescription("description");
//        spaService.setDuration(0L);
//        spaService.setIsActive(false);
//        spaService.setCurrentPrice(0.0);
//        spaService.setBookedCount(0L);
//        final EquipmentType equipmentType = new EquipmentType();
//        equipmentType.setId(0L);
//        spaService.setEquipmentType(equipmentType);
//        category.setSpaServices(List.of(spaService));
//        final List<Category> categories = List.of(category);
//        when(mockCategoryRepository.findAll()).thenReturn(categories);
//
//        // Configure EquipmentTypeRepository.findAll(...).
//        final EquipmentType equipmentType1 = new EquipmentType();
//        equipmentType1.setId(0L);
//        equipmentType1.setName("name");
//        equipmentType1.setCode("code");
//        equipmentType1.setDescription("description");
//        final EquipmentBranch equipmentBranch = new EquipmentBranch();
//        equipmentBranch.setId(0L);
//        final Branch branch = new Branch();
//        branch.setId(0L);
//        branch.setName("name");
//        branch.setCode("code");
//        branch.setDetailAddress("detailAddress");
//        branch.setCountry("country");
//        branch.setLatitude(0.0);
//        branch.setLongitude(0.0);
//        branch.setHotline("hotline");
//        equipmentBranch.setBranch(branch);
//        equipmentType1.setEquipmentBranches(Set.of(equipmentBranch));
//        final List<EquipmentType> equipmentTypes = List.of(equipmentType1);
//        when(mockEquipmentTypeRepository.findAll()).thenReturn(equipmentTypes);
//
//        // Configure LookupRepository.findByLookupKey(...).
//        final List<Lookup> lookups = List.of(new Lookup(0L, "lookupKey", "code", "name", "description"));
//        when(mockLookupRepository.findByLookupKey("APPOINTMENT_MASTER_STATUS")).thenReturn(lookups);
//
//        when(mockReasonMessageRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Configure BranchRepository.findAllByIsActive(...).
//        final Branch branch1 = new Branch();
//        branch1.setId(0L);
//        branch1.setName("name");
//        branch1.setCode("code");
//        branch1.setDetailAddress("detailAddress");
//        branch1.setCountry("country");
//        branch1.setLatitude(0.0);
//        branch1.setLongitude(0.0);
//        branch1.setHotline("hotline");
//        branch1.setIsActive(false);
//        final ConfigData configData = new ConfigData();
//        configData.setId(0L);
//        configData.setConfigKey("configKey");
//        configData.setConfigValue("configValue");
//        configData.setConfigDesc("configDesc");
//        configData.setAllowUpdate(false);
//        branch1.setConfigDataSet(Set.of(configData));
//        final List<Branch> branchList = List.of(branch1);
//        when(mockBranchRepository.findAllByIsActive(Boolean.TRUE)).thenReturn(branchList);
//
//        // Run the test
//        final ResponseDTO result = commonServiceImplUnderTest.getSelectionByType("type");
//
//        // Verify the results
//    }
//
//    @Test
//    public void testGetSelectionByType_BranchRepositoryReturnsNoItems() {
//        // Setup
//        // Configure CategoryRepository.findAll(...).
//        final Category category = new Category();
//        category.setId(0L);
//        category.setName("name");
//        category.setCode("code");
//        category.setDescription("description");
//        final SpaService spaService = new SpaService();
//        spaService.setId(0L);
//        spaService.setName("name");
//        spaService.setCode("code");
//        spaService.setDescription("description");
//        spaService.setDuration(0L);
//        spaService.setIsActive(false);
//        spaService.setCurrentPrice(0.0);
//        spaService.setBookedCount(0L);
//        final EquipmentType equipmentType = new EquipmentType();
//        equipmentType.setId(0L);
//        spaService.setEquipmentType(equipmentType);
//        category.setSpaServices(List.of(spaService));
//        final List<Category> categories = List.of(category);
//        when(mockCategoryRepository.findAll()).thenReturn(categories);
//
//        // Configure EquipmentTypeRepository.findAll(...).
//        final EquipmentType equipmentType1 = new EquipmentType();
//        equipmentType1.setId(0L);
//        equipmentType1.setName("name");
//        equipmentType1.setCode("code");
//        equipmentType1.setDescription("description");
//        final EquipmentBranch equipmentBranch = new EquipmentBranch();
//        equipmentBranch.setId(0L);
//        final Branch branch = new Branch();
//        branch.setId(0L);
//        branch.setName("name");
//        branch.setCode("code");
//        branch.setDetailAddress("detailAddress");
//        branch.setCountry("country");
//        branch.setLatitude(0.0);
//        branch.setLongitude(0.0);
//        branch.setHotline("hotline");
//        equipmentBranch.setBranch(branch);
//        equipmentType1.setEquipmentBranches(Set.of(equipmentBranch));
//        final List<EquipmentType> equipmentTypes = List.of(equipmentType1);
//        when(mockEquipmentTypeRepository.findAll()).thenReturn(equipmentTypes);
//
//        // Configure LookupRepository.findByLookupKey(...).
//        final List<Lookup> lookups = List.of(new Lookup(0L, "lookupKey", "code", "name", "description"));
//        when(mockLookupRepository.findByLookupKey("APPOINTMENT_MASTER_STATUS")).thenReturn(lookups);
//
//        // Configure ReasonMessageRepository.findAll(...).
//        final List<ReasonMessage> reasonMessages = List.of(
//            new ReasonMessage(0L, "name", "code", "description", false, false,
//                new ReasonMessageType(0L, "name", "code", "description")));
//        when(mockReasonMessageRepository.findAll()).thenReturn(reasonMessages);
//
//        when(mockBranchRepository.findAllByIsActive(Boolean.TRUE)).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final ResponseDTO result = commonServiceImplUnderTest.getSelectionByType("type");
//
//        // Verify the results
//    }
//}
