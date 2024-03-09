package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_activity")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserActivity extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JsonIgnoreProperties(value = "userActivities", allowSetters = true)
    @JoinColumn(name = "working_status_id")
    private WorkingStatus status;

}
