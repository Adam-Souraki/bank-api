package org.souraki.bankapi.controler.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.souraki.bankapi.domain.dto.account.AccountResponseDTO;
import org.souraki.bankapi.domain.dto.account.DepositRequestDTO;
import org.souraki.bankapi.domain.dto.account.CreateAccountRequestDTO;
import org.souraki.bankapi.domain.dto.account.TransferRequestDTO;
import org.souraki.bankapi.domain.entity.Account;
import org.souraki.bankapi.domain.mapper.AccountMapper;
import org.souraki.bankapi.service.account.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Operations related to bank accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create a new bank account", description = "Creates a new bank account with an account number, first name, and last name.")
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody CreateAccountRequestDTO request) {
        Account account = accountService.createAccount(request.getAccountNumber(), request.getFirstName(), request.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountMapper.toDTO(account));
    }

    @Operation(summary = "Deposit money to an account", description = "Deposits a positive amount (in DKK) into the specified account.")
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@PathVariable String accountNumber, @RequestBody @Valid DepositRequestDTO request
    ) {
        var account = accountService.deposit(accountNumber, request.getAmount());
        return ResponseEntity.ok(AccountMapper.toDTO(account));
    }

    @Operation(summary = "Transfer money between accounts", description = "Transfers a positive amount from one account to another.")
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequestDTO request) {
        accountService.transfer(request.getFromAccount(), request.getToAccount(), request.getAmount());
        return ResponseEntity.ok("Transfer successful");
    }

    @Operation(summary = "Get account balance", description = "Retrieves the current balance for the specified account.")
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    @Operation(summary = "Get all accounts", description = "Retrieves a list of all bank accounts.")
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> accounts = accountService.getAllAccounts().stream().map(AccountMapper::toDTO).toList();
        return ResponseEntity.ok(accounts);
    }

}

