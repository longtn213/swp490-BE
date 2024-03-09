package com.fpt.ssds.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sca_result")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ScaResult extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JsonIgnoreProperties(value = "scaResults", allowSetters = true)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JsonIgnoreProperties(value = "scaResults", allowSetters = true)
    @JoinColumn(name = "replied_by_id")
    private User repliedBy;

    @ManyToOne
    @JsonIgnoreProperties(value = "scaResults", allowSetters = true)
    @JoinColumn(name = "status_id")
    private Lookup status;

    @OneToMany(mappedBy = "result")
    @JsonIgnore
    private List<QuestionAnswer> answerSet = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "sca_result_service",
        joinColumns = @JoinColumn(name = "sca_result_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "service_id", referencedColumnName = "id"))
    private List<SpaService> spaServices = new ArrayList<>();
}
