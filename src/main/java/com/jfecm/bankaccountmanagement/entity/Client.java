package com.jfecm.bankaccountmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client in the banking system.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clients",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Client {

    /**
     * The unique identifier for the client.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The DNI (identification number) of the client.
     */
    @Column
    private String dni;

    /**
     * The name of the client.
     */
    @Column
    private String name;

    /**
     * The email address of the client.
     */
    @Column(unique = true)
    private String email;

    /**
     * The password associated with the client's account.
     */
    @Column
    private String password;

    /**
     * The address of the client.
     */
    @Column
    private String address;

    /**
     * The status of the client's account (e.g., active, inactive).
     */
    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    /**
     * The banking account associated with the client.
     */
    @OneToOne(mappedBy = "client", cascade = CascadeType.PERSIST)
    private BankingAccount bankingAccount;

    /**
     * The list of adherents (clients associated with this client).
     */
    @OneToMany(mappedBy = "mainClient")
    private List<Client> adherents = new ArrayList<>();

    /**
     * The main client (if this client is an adherent).
     */
    @ManyToOne
    @JoinColumn(name = "main_client_id")
    @JsonIgnore
    private Client mainClient;
}
