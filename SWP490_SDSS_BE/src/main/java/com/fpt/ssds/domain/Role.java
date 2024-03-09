package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Role extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "role_permission",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<User> users = new ArrayList<>();
}
