package com.jfecm.bankaccountmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a banking account.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banking_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = "account_number"))
public class BankingAccount {

    /**
     * The unique identifier for the banking account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The account number associated with the banking account.
     */
    @Column(name = "account_number", unique = true)
    private String accountNumber;

    /**
     * The balance of the banking account.
     */
    @Column
    private Double balance;

    /**
     * The overdraft limit for the banking account.
     */
    @Column
    private Double overdraftLimit;

    /**
     * The date when the banking account was opened.
     */
    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate accountOpenedDate;

    /**
     * The date when the banking account was closed.
     */
    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate accountClosingDate;

    /**
     * The client associated with the banking account.
     */
    @OneToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    /**
     * The status of the banking account (e.g., active, inactive).
     */
    @Column
    @Enumerated(EnumType.STRING)
    private BankingAccountStatus bankingAccountStatus;

    /**
     * The list of account transactions associated with the banking account.
     */
    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL)
    private List<AccountTransaction> accountTransactions = new ArrayList<>();
}
