package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "appointment_master")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AppointmentMaster extends AbstractAuditingEntity {
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

    @Column(name = "overdue_status")
    private String overdueStatus;

    @Column(name = "note")
    private String note;

    @Column(name = "confirm_message_sent")
    private Boolean confirmMessageSent;

    @Column(name = "payment_method")
    private String paymentMethod;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentMasters", allowSetters = true)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentMasters", allowSetters = true)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentMasters", allowSetters = true)
    @JoinColumn(name = "session_id")
    private Session session;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentMasters", allowSetters = true)
    @JoinColumn(name = "status_id")
    private Lookup status;

    @OneToMany(mappedBy = "appointmentMaster", cascade = CascadeType.ALL)
    private List<AppointmentService> appointmentServices = new ArrayList<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentMasters", allowSetters = true)
    @JoinColumn(name = "canceled_reason_id")
    private ReasonMessage canceledReason;
}
