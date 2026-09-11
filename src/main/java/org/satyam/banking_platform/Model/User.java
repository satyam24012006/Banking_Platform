package org.satyam.banking_platform.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String username;
    @Column(nullable=false,unique=true)
    private String email;
    @Column(nullable=false)
    private String password;
    @Column(nullable=false,unique=true)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;
    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
    @OneToMany(mappedBy = "user")
    private List<Beneficiary> beneficiaries;
    @OneToMany(mappedBy = "user")
    private List<AuditLog> auditLogs;
    @Column(nullable = false)
    private boolean active;
}