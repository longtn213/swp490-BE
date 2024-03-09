package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "reason_mapping")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReasonMapping extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_id")
    private Long refId;

    @Column(name = "ref_type")
    private String refType;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "reason_note")
    private String reasonNote;

    @ManyToOne
    @JsonIgnoreProperties(value = "reasonMappings", allowSetters = true)
    @JoinColumn(name = "reason_id")
    private ReasonMessage reasonMessage;
}
