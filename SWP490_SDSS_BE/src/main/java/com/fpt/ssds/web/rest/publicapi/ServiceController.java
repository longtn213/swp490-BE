package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.SpaServiceService;
import com.fpt.ssds.service.criteria.ServiceCriteria;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.queryservice.SpaServiceQueryService;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("PublicServiceController")
@RequestMapping("${ssds.api.ref.public}/web/v1/service")
@Slf4j
public class ServiceController {
    private final SpaServiceService spaServiceService;

    private final SpaServiceQueryService spaServiceQueryService;

    @Autowired
    public ServiceController(SpaServiceService spaServiceService, SpaServiceQueryService spaServiceQueryService) {
        this.spaServiceService = spaServiceService;
        this.spaServiceQueryService = spaServiceQueryService;
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(ResponseUtils.responseOK(spaServiceService.getById(id)));
    }

    @GetMapping()
    ResponseEntity<ResponseDTO> findByCriteria(ServiceCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Service by criteria: {}", criteria);
        Page<SpaServiceDto> page = spaServiceQueryService.findByCriteria(criteria, pageable);
        ResponseDTO response = ResponseUtils.responseOK(page.getContent());
        response.getMeta().setTotal(page.getTotalElements());
        response.getMeta().setPage(page.getNumber());
        response.getMeta().setSize(page.getSize());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
