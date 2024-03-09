package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "reason_message")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReasonMessage extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "require_note")
    private Boolean requireNote;

    @ManyToOne
    @JsonIgnoreProperties(value = "reasonMessages", allowSetters = true)
    @JoinColumn(name = "type_id")
    private ReasonMessageType reasonMessageType;
}
