package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "appointment_service")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AppointmentService extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expected_start_time")
    private Instant expectedStartTime;

    @Column(name = "expected_end_time")
    private Instant expectedEndTime;

    @Column(name = "actual_start_time")
    private Instant actualStartTime;

    @Column(name = "actual_end_time")
    private Instant actualEndTime;

    @Column(name = "cancel_time")
    private Instant cancelTime;

    @Column(name = "cancel_by")
    private String cancelBy;

    @Column(name = "total")
    private Double total;

    @Column(name = "pay_amount")
    private Double payAmount;

    @Column(name = "note")
    private String note;

    @Column(name = "`order`")
    private Integer order;

    @Column(name = "specialist_info_note")
    private String specialistInfoNote;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentServices", allowSetters = true)
    @JoinColumn(name = "appointment_master_id")
    private AppointmentMaster appointmentMaster;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentServices", allowSetters = true)
    @JoinColumn(name = "service_id")
    private SpaService spaService;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentServices", allowSetters = true)
    @JoinColumn(name = "status_id")
    private Lookup status;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentServices", allowSetters = true)
    @JoinColumn(name = "canceled_reason_id")
    private ReasonMessage canceledReason;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentServices", allowSetters = true)
    @JoinColumn(name = "specialist_id")
    private User specialist;
}
