package org.souraki.bankapi.service.account;

import org.souraki.bankapi.domain.entity.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account createAccount(String accountNumber, String firstName, String lastName);

    Account deposit(String accountNumber, BigDecimal amount);

    void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);

    BigDecimal getBalance(String accountNumber);

    List<Account> getAllAccounts();
}
