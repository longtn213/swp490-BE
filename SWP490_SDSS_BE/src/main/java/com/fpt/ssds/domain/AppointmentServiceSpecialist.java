package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "appointment_service_specialist")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AppointmentServiceSpecialist extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentServiceSpecialists", allowSetters = true)
    @JoinColumn(name = "specialist_id")
    private User specialist;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentServiceSpecialists", allowSetters = true)
    @JoinColumn(name = "appointment_service_id")
    private AppointmentService appointmentService;
}
