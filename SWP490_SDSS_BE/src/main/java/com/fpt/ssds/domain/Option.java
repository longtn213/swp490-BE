package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "`option`")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Option extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`option`")
    private String option;

    @Column(name = "`order`")
    private Integer order;

    @ManyToOne
    @JsonIgnoreProperties(value = "options", allowSetters = true)
    @JoinColumn(name = "question_id")
    private ScaQuestion question;
}
