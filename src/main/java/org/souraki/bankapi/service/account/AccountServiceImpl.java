package org.souraki.bankapi.service.account;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.souraki.bankapi.domain.entity.Account;
import org.souraki.bankapi.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository,  MeterRegistry meterRegistry) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Timed(value = "bank.account.create.time", description = "Time taken to create account")
    @Counted(value = "bank.account.create.count", description = "Number of accounts created")
    @Transactional
    public Account createAccount(String accountNumber, String firstName, String lastName) {
        if (accountRepository.existsById(accountNumber)) {
            throw new IllegalArgumentException("Account already exists");
        }
        Account account = new Account(accountNumber, firstName, lastName, BigDecimal.ZERO);
        log.info("Creating account {}", accountNumber);
        return accountRepository.save(account);
    }

    @Override
    @Timed(value = "bank.account.deposit.time", description = "Time taken to deposit amount")
    @Counted(value = "bank.account.deposit.count", description = "Number of deposits")
    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        BigDecimal balance = account.getBalance();
        account.setBalance(balance.add(amount));
        log.info("Deposited {} to {}", amount, accountNumber);
        return account;
    }

    @Override
    @Timed(value = "bank.account.transfer.time", description = "Time taken to transfer amount")
    @Counted(value = "bank.account.transfer.count", description = "Number of transfers")
    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account from = accountRepository.findById(fromAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account to = accountRepository.findById(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        log.info("Transferred {} from {} to {}", amount, fromAccountNumber, toAccountNumber);
    }

    @Override
    @Timed(value = "bank.account.getBalance.time", description = "Time taken to get balance")
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return account.getBalance();
    }

    @Override
    @Timed(value = "bank.account.getAll.time", description = "Time taken to list all accounts")
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

}
