package com.jfecm.bankaccountmanagement.controller;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateTransaction;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateTransaction;
import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import com.jfecm.bankaccountmanagement.service.BankingAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts/account/{accountNumber}/transactions")
public class AccountTransactionController {
    private final BankingAccountService bankingAccountService;

    /**
     * Recharge the balance of a bank account.
     *
     * @param accountNumber Bank account number.
     * @param amount        Amount to recharge.
     * @return ResponseEntity with the reload result.
     */
    @PostMapping("/transaction/recharge/{amount}")
    public ResponseEntity<Map<String, Object>> recharge(@PathVariable String accountNumber,
                                                        @PathVariable Double amount) {
        AccountTransaction depositTransaction = bankingAccountService.rechargeAccountBalance(accountNumber, amount);
        return ResponseEntity.ok(Map.of("Result", depositTransaction));
    }

    /**
     * Make a withdrawal transaction from a bank account.
     *
     * @param accountNumber Bank account number.
     * @param amount        Amount to withdraw.
     * @return ResponseEntity with the result of the withdrawal transaction.
     */
    @PostMapping("/transaction/withdrawal/{amount}")
    public ResponseEntity<Map<String, Object>> withdrawal(@PathVariable String accountNumber,
                                                          @PathVariable Double amount) {
        AccountTransaction withdrawalTransaction = bankingAccountService.createWithdrawalTransaction(accountNumber, amount);
        return ResponseEntity.ok(Map.of("Result", withdrawalTransaction));
    }

    /**
     * Make a transfer transaction between bank accounts.
     *
     * @param accountNumber   Source bank account number.
     * @param requestTransfer Transfer information.
     * @return ResponseEntity with the result of the transfer transaction.
     */
    @PostMapping("/transaction/transfer")
    public ResponseEntity<Map<String, Object>> transfer(@PathVariable String accountNumber,
                                                        @RequestBody RequestCreateTransaction requestTransfer) {
        AccountTransaction transferTransaction = bankingAccountService.createTransferTransaction(accountNumber, requestTransfer);
        return ResponseEntity.ok(Map.of("Result", transferTransaction));
    }

    /**
     * Gets a specific transaction by its ID.
     *
     * @param accountNumber Bank account number.
     * @param transactionId ID of the transaction.
     * @return ResponseEntity with the transaction found.
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransactionByAccountNumber(@PathVariable String accountNumber,
                                                                             @PathVariable Long transactionId) {
        AccountTransaction transaction = bankingAccountService.getTransactionByAccountNumber(accountNumber, transactionId);
        return ResponseEntity.ok(Map.of("Result", transaction));
    }

    /**
     * Updates an existing transaction.
     *
     * @param accountNumber      Bank account number.
     * @param transactionId      ID of the transaction to update.
     * @param accountTransaction Updated transaction data.
     * @return ResponseEntity with the updated transaction.
     */
    @PutMapping("/transaction/{transactionId}")
    public ResponseEntity<Map<String, Object>> updateTransaction(@PathVariable String accountNumber,
                                                                 @PathVariable Long transactionId,
                                                                 @RequestBody RequestUpdateTransaction accountTransaction) {
        AccountTransaction accountUpdated = bankingAccountService.updateTransaction(accountNumber, transactionId, accountTransaction);
        return ResponseEntity.ok(Map.of("Result", accountUpdated));
    }

    /**
     * Delete a transaction by its ID.
     *
     * @param accountNumber Bank account number.
     * @param transactionId ID of the transaction to delete.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/transaction/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(@PathVariable String accountNumber,
                                                                 @PathVariable Long transactionId) {
        bankingAccountService.deleteTransaction(accountNumber, transactionId);
        return ResponseEntity.ok(Map.of("Result", "Transaction deleted"));
    }

    /**
     * Gets all transactions from a bank account.
     *
     * @param accountNumber Bank account number.
     * @return ResponseEntity with the list of transactions.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTransactionsByAccount(@PathVariable String accountNumber) {
        List<AccountTransaction> transactions = bankingAccountService.getAllTransactionsByAccount(accountNumber);

        if (transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(Map.of("Total", transactions.size(), "Result", transactions), HttpStatus.OK);
    }

    /**
     * Filter transactions from a bank account by type.
     *
     * @param accountNumber         Bank account number.
     * @param transactionTypeFilter Type of transaction to filter.
     * @return ResponseEntity with the list of filtered transactions.
     */
    @GetMapping("/filterByType/{transactionTypeFilter}")
    public ResponseEntity<Map<String, Object>> filterTransactionsByType(@PathVariable String accountNumber,
                                                                        @PathVariable AccountTransactionType transactionTypeFilter) {
        List<AccountTransaction> transactions = bankingAccountService.getAllTransactionsByType(accountNumber, transactionTypeFilter);

        if (!transactions.isEmpty()) {
            return new ResponseEntity<>(Map.of("Total", transactions.size(), "Result", transactions), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Filter transactions from a bank account by date range.
     *
     * @param accountNumber Bank account number.
     * @param fromDate      Start date of the range.
     * @param toDate        End date of range.
     * @return ResponseEntity with list of transactions filtered by date.
     */
    @GetMapping("/filterByDateRange")
    public ResponseEntity<Map<String, Object>> filterTransactionsByDateRange(@PathVariable String accountNumber,
                                                                             @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                             @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            return new ResponseEntity<>(Map.of("Result", "Check date filters"), HttpStatus.BAD_REQUEST);
        }

        List<AccountTransaction> transactions = bankingAccountService.getAllTransactionsByDateRange(accountNumber, fromDate, toDate);

        if (!transactions.isEmpty()) {
            return new ResponseEntity<>(Map.of("Total", transactions.size(), "Result", transactions), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Filters bank account transactions by type and date range.
     *
     * @param accountNumber         Bank account number.
     * @param transactionTypeFilter Type of transaction to filter.
     * @param fromDate              Start date of the range.
     * @param toDate                End date of range.
     * @return ResponseEntity with the list of transactions filtered by type and date.
     */
    @GetMapping("/filterByTypeAndDateRange")
    public ResponseEntity<Map<String, Object>> filterTransactionsByTypeAndDateRange(@PathVariable String accountNumber,
                                                                                    @RequestParam AccountTransactionType transactionTypeFilter,
                                                                                    @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                    @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        if (transactionTypeFilter == null || fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            return new ResponseEntity<>(Map.of("Result", "Check date filters & type must not be null."), HttpStatus.BAD_REQUEST);
        }

        List<AccountTransaction> transactions = bankingAccountService.getAllTransactionsByTypeAndDateRange(accountNumber, transactionTypeFilter, fromDate, toDate);

        if (transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(Map.of("Total", transactions.size(), "Result", transactions), HttpStatus.OK);

    }
}
