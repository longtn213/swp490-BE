package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class User extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "dob")
    private Instant dob;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "email")
    private String email;

    @Column(name = "refId")
    private String refId;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private List<Notification> notifications = new ArrayList<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "users", allowSetters = true)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JsonIgnoreProperties(value = "users", allowSetters = true)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "specialist")
    private List<AppointmentService> appointmentServices = new ArrayList<>();
}
