package com.fpt.ssds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "feedback")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Feedback extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point")
    private Float point;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "ref_id")
    private Long refId;

    @Column(name = "ref_type")
    private String refType;
}
