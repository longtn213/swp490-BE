package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "config_data")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ConfigData extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key")
    private String configKey;

    @Column(name = "config_value")
    private String configValue;

    @Column(name = "description")
    private String configDesc;

    @Column(name = "allow_update")
    private Boolean allowUpdate;

    @Column(name = "type")
    private String type;

    @ManyToOne
    @JsonIgnoreProperties(value = "configDatas", allowSetters = true)
    @JoinColumn(name = "branch_id")
    private Branch branch;

}
