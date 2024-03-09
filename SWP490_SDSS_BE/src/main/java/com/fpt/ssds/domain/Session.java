package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "session")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Session extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order")
    private Integer order;

    @Column(name = "expected_start_time")
    private Instant expectedStartTime;

    @ManyToOne
    @JsonIgnoreProperties(value = "sessions", allowSetters = true)
    @JoinColumn(name = "course_id")
    private SpaCourse spaCourse;

    @ManyToOne
    @JsonIgnoreProperties(value = "sessions", allowSetters = true)
    @JoinColumn(name = "service_id")
    private SpaService spaService;

    @ManyToOne
    @JsonIgnoreProperties(value = "sessions", allowSetters = true)
    @JoinColumn(name = "status_id")
    private Lookup status;
}
