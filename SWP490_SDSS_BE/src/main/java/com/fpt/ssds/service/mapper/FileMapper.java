package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.File;
import com.fpt.ssds.service.dto.FileDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface FileMapper extends EntityMapper<FileDto, File> {

    default File fromId(Long id) {
        if (id == null) {
            return null;
        }
        File file = new File();
        file.setId(id);
        return file;
    }
}
