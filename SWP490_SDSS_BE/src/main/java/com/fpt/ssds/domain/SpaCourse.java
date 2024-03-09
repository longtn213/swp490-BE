package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "spa_course")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SpaCourse extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "cancel_time")
    private Instant cancelTime;

    @Column(name = "cancel_by")
    private String cancelBy;

    @ManyToOne
    @JsonIgnoreProperties(value = "spaCourses", allowSetters = true)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JsonIgnoreProperties(value = "spaCourses", allowSetters = true)
    @JoinColumn(name = "status_id")
    private Lookup status;

    @ManyToOne
    @JsonIgnoreProperties(value = "spaCourses", allowSetters = true)
    @JoinColumn(name = "customer_id")
    private User customer;

    @OneToMany(mappedBy = "spaCourse")
    @JsonIgnore
    private Set<Session> sessions = new HashSet<>();
}
