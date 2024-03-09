package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "appointment_tracking")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AppointmentTracking extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`time`")
    private Instant time;

    @Column(name = "max_qty")
    private Long maxQty;

    @Column(name = "booked_qty")
    private Long bookedQty;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "is_first_time_in_day")
    private Boolean isFirstTimeInDay;

    @Column(name = "is_last_time_in_day")
    private Boolean isLastTimeInDay;

    @ManyToOne
    @JsonIgnoreProperties(value = "appointmentTrackings", allowSetters = true)
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
