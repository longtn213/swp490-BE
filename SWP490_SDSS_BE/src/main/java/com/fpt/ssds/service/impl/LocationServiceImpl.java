package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.Location;
import com.fpt.ssds.repository.LocationRepository;
import com.fpt.ssds.service.LocationService;
import com.fpt.ssds.service.dto.GenerateLocationDto;
import com.fpt.ssds.service.dto.LocationDTO;
import com.fpt.ssds.service.mapper.LocationMapper;
import com.fpt.ssds.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    @Override
    public String generateLocation(List<GenerateLocationDto> generateLocationDtos) {
        String result = "";
        Integer i = 1;
        result = generateLocation(generateLocationDtos, result, i);
        return result;
    }

    private String generateLocation(List<GenerateLocationDto> generateLocationDtos, String result, Integer i) {
        for (GenerateLocationDto generateLocationDto : generateLocationDtos) {
            List<String> listLocationName = generateLocationDto.getObjectName();
            String locationType = generateLocationDto.getLocationType();
            String parentCode = generateLocationDto.getParentCode();
            long prefixId = Instant.now().toEpochMilli();
            for (String name : listLocationName) {
                String code = parentCode + "_" + Utils.genCodeFromName(name);
                result += "<changeSet author=\"trinhpk\" id=\"" + prefixId + "-" + i + "\">\n" +
                    "        <preConditions onFail=\"MARK_RAN\">\n" +
                    "            <sqlCheck expectedResult=\"0\">\n" +
                    "                SELECT COUNT(*)\n" +
                    "                FROM location\n" +
                    "                WHERE division_code = '" + code + "'\n" +
                    "                  and division_level = '" + locationType + "'\n" +
                    "            </sqlCheck>\n" +
                    "        </preConditions>\n" +
                    "        <insert tableName=\"location\">\n" +
                    "            <column name=\"division_name\" value=\"" + name + "\"/>\n" +
                    "            <column name=\"division_code\" value=\"" + code + "\"/>\n" +
                    "            <column name=\"division_level\" value=\"" + locationType + "\"/>\n" +
                    "            <column name=\"division_parent_id\"\n" +
                    "                    valueComputed=\"(select id from location l1 where division_code = '" + parentCode + "')\"/>\n" +
                    "            <column name=\"created_by\" value=\"system\"/>\n" +
                    "            <column name=\"created_date\" value=\"2022-10-21 19:45:35.000000\"/>\n" +
                    "            <column name=\"last_modified_by\" value=\"system\"/>\n" +
                    "            <column name=\"last_modified_date\" value=\"2022-10-21 19:45:35.000000\"/>\n" +
                    "        </insert>\n" +
                    "    </changeSet>\n\n";
                i++;
            }
        }
        return result;
    }

    @Override
    public List<LocationDTO> getChildDivisionList(Long divisionId) {
        List<Location> locations = new ArrayList<>();
        if (Objects.isNull(divisionId) || NumberUtils.LONG_ZERO.equals(divisionId)) {
            locations = locationRepository.findAllByDivisionParentIdIsNull();
        } else {
            locations = locationRepository.findAllByDivisionParentIdIn(Arrays.asList(divisionId));
        }
        return locationMapper.toDto(locations);
    }
}
