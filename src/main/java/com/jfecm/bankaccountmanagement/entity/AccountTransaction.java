package com.jfecm.bankaccountmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a banking account transaction.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_transactions")
public class AccountTransaction {

    /**
     * The unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The type of account transaction (e.g., deposit, withdrawal).
     */
    @Column
    @Enumerated(EnumType.STRING)
    private AccountTransactionType accountTransactionType;

    /**
     * The date of execution of the transaction.
     */
    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfExecution;

    /**
     * The time of execution of the transaction.
     */
    @Column
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime timeOfExecution;

    /**
     * The amount associated with the transaction.
     */
    @Column
    private Double amount;

    /**
     * The banking account associated with the transaction.
     */
    @ManyToOne
    @JoinColumn(name = "banking_account_id")
    @JsonIgnore
    private BankingAccount bankingAccount;
}
