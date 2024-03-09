package com.fpt.ssds.domain;

import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.StorageSource;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "file")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class File extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metadata")
    private String metadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FileType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_source")
    private StorageSource storageSource;

    @Column(name = "upload_status")
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;

    @Column(name = "fail_reason")
    private String failReason;

    @Column(name = "start_upload_time")
    private Instant startUploadTime;

    @Column(name = "finish_upload_time")
    private Instant finishUploadTime;

    @Column(name = "url")
    private String url;

    @Column(name = "size")
    private Double size;

    @Column(name = "refId")
    private Long refId;
}
