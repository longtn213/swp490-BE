package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.LocationService;
import com.fpt.ssds.service.dto.GenerateLocationDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("PublicLocationController")
@RequestMapping("${ssds.api.ref.internal}/web/v1/location")
@Slf4j
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping("generate")
    public ResponseEntity<ResponseDTO> generateLocation(@RequestBody List<GenerateLocationDto> generateLocationDtos) {
        String result = locationService.generateLocation(generateLocationDtos);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(result));
    }

    @GetMapping("get_child_division_list")
    public ResponseEntity<ResponseDTO> getChildDivisionList(@RequestParam("division_id") Long divisionId) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(locationService.getChildDivisionList(divisionId)));
    }
}
