package org.souraki.bankapi.service.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.souraki.bankapi.domain.entity.Account;
import org.souraki.bankapi.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.souraki.bankapi.util.TestData.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccountSuccess() {
        when(accountRepository.existsById(ACCOUNT_A)).thenReturn(false);

        Account saved = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_ZERO);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        Account result = accountService.createAccount(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBalance());
        assertEquals(ACCOUNT_A, result.getAccountNumber());
        assertEquals(FIRSTNAME_A, result.getFirstName());
        assertEquals(LASTNAME_A, result.getLastName());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testCreateAccountAlreadyExists() {
        when(accountRepository.existsById(ACCOUNT_A)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A));
    }

    @Test
    void testDepositSuccess() {
        Account existing = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_100);
        when(accountRepository.findById(ACCOUNT_A)).thenReturn(Optional.of(existing));

        Account result = accountService.deposit(ACCOUNT_A, AMOUNT_50);

        assertEquals(AMOUNT_150, result.getBalance());
    }

    @Test
    void testDepositNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(ACCOUNT_A, AMOUNT_NEGATIVE));
    }

    @Test
    void testTransferSuccess() {
        Account from = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_200);
        Account to = new Account(ACCOUNT_B, FIRSTNAME_B, LASTNAME_B, AMOUNT_100);

        when(accountRepository.findById(ACCOUNT_A)).thenReturn(Optional.of(from));
        when(accountRepository.findById(ACCOUNT_B)).thenReturn(Optional.of(to));

        accountService.transfer(ACCOUNT_A, ACCOUNT_B, AMOUNT_50);

        assertEquals(AMOUNT_150, from.getBalance());
        assertEquals(AMOUNT_150, to.getBalance());
    }

    @Test
    void testTransferInsufficientFunds() {
        Account from = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_50);
        Account to = new Account(ACCOUNT_B, FIRSTNAME_B, LASTNAME_B, AMOUNT_200);

        when(accountRepository.findById(ACCOUNT_A)).thenReturn(Optional.of(from));
        when(accountRepository.findById(ACCOUNT_B)).thenReturn(Optional.of(to));

        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(ACCOUNT_A, ACCOUNT_B, AMOUNT_100));
    }

    @Test
    void testGetBalanceSuccess() {
        Account account = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_200);
        when(accountRepository.findById(ACCOUNT_A)).thenReturn(Optional.of(account));

        BigDecimal balance = accountService.getBalance(ACCOUNT_A);

        assertEquals(AMOUNT_200, balance);
    }

    @Test
    void testGetBalanceAccountNotFound() {
        when(accountRepository.findById("ACCOUNT_X")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> accountService.getBalance("ACCOUNT_X"));
    }
}
