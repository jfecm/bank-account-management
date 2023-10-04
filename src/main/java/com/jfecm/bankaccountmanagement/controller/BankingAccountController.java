package com.jfecm.bankaccountmanagement.controller;

import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.service.BankingAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
public class BankingAccountController {
    private final BankingAccountService bankingAccountService;

    /**
     * Retrieves a banking account by its account number.
     *
     * @param accountNumber The account number to retrieve.
     * @return ResponseEntity with the banking account if found.
     */
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<Map<String, Object>> getBankingAccount(@PathVariable String accountNumber) {

        BankingAccount account = bankingAccountService.getBankingAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(Map.of("Result", account));
    }

    /**
     * Retrieves a list of banking accounts filtered by status.
     *
     * @param status The status to filter banking accounts (default: ACTIVE).
     * @return ResponseEntity with the list of banking accounts.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBankingAccounts(@RequestParam(required = false, defaultValue = "ACTIVE") String status) {
        List<BankingAccount> accounts = bankingAccountService.getAllBankingAccounts(status);

        if (accounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(Map.of("Total", accounts.size(), "Result", accounts), HttpStatus.OK);
    }

    /**
     * Updates the status of a banking account by its account number.
     *
     * @param accountNumber    The account number to update.
     * @param newAccountStatus The new status to set for the account.
     * @return ResponseEntity with a success message.
     */
    @PutMapping("/account/{accountNumber}/status/{newAccountStatus}")
    public ResponseEntity<Map<String, Object>> updateBankingAccountStatus(@PathVariable String accountNumber,
                                                                          @PathVariable String newAccountStatus) {
        bankingAccountService.updateBankingAccountStatusByAccountNumber(accountNumber, newAccountStatus);
        return ResponseEntity.ok(Map.of("Result", "Banking account status updated."));
    }

    /**
     * Deletes a banking account by changing its status.
     *
     * @param number The account number to delete.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/account/{number}")
    public ResponseEntity<Map<String, Object>> deleteBankingAccount(@PathVariable String number) {
        // Deleting an account is only the change of user status.
        bankingAccountService.deleteBankingAccount(number);
        return ResponseEntity.ok(Map.of("Result", "Banking account deleted."));
    }
}
