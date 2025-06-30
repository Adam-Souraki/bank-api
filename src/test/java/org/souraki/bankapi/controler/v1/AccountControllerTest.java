package org.souraki.bankapi.controler.v1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.souraki.bankapi.domain.dto.account.DepositRequestDTO;
import org.souraki.bankapi.domain.dto.account.CreateAccountRequestDTO;
import org.souraki.bankapi.domain.dto.account.TransferRequestDTO;
import org.souraki.bankapi.domain.entity.Account;
import org.souraki.bankapi.service.account.AccountService;
import org.souraki.bankapi.util.TestData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.souraki.bankapi.util.TestData.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount() {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A);
        Account expectedAccount = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_ZERO);
        when(accountService.createAccount(TestData.ACCOUNT_A, TestData.FIRSTNAME_A, TestData.LASTNAME_A))
                .thenReturn(expectedAccount);

        ResponseEntity<?> response = accountController.createAccount(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains(FIRSTNAME_A, LASTNAME_A, ACCOUNT_A);
    }

    @Test
    void testDeposit() {
        DepositRequestDTO request = new DepositRequestDTO(AMOUNT_100);
        Account updatedAccount = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_100);
        when(accountService.deposit(TestData.ACCOUNT_A, TestData.AMOUNT_100)).thenReturn(updatedAccount);

        ResponseEntity<?> response = accountController.deposit(TestData.ACCOUNT_A, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains(FIRSTNAME_A, LASTNAME_A, ACCOUNT_A, AMOUNT_100.toPlainString());
    }

    @Test
    void testTransfer() {
        TransferRequestDTO request = new TransferRequestDTO(ACCOUNT_A, ACCOUNT_B, AMOUNT_50);
        doNothing().when(accountService).transfer(ACCOUNT_A, ACCOUNT_B, AMOUNT_50);

        ResponseEntity<String> response = accountController.transfer(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Transfer successful");
        verify(accountService).transfer(ACCOUNT_A, ACCOUNT_B, AMOUNT_50);
    }

    @Test
    void testGetBalance() {
        when(accountService.getBalance(ACCOUNT_A)).thenReturn(AMOUNT_200);

        ResponseEntity<BigDecimal> response = accountController.getBalance(ACCOUNT_A);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualByComparingTo(AMOUNT_200);
    }

    @Test
    void testGetAllAccounts() {
        Account account1 = new Account(ACCOUNT_A, FIRSTNAME_A, LASTNAME_A, AMOUNT_100);
        Account account2 = new Account(ACCOUNT_B, FIRSTNAME_B, LASTNAME_B, AMOUNT_200);
        when(accountService.getAllAccounts()).thenReturn(List.of(account1, account2));

        ResponseEntity<?> response = accountController.getAllAccounts();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains(FIRSTNAME_A, FIRSTNAME_B, ACCOUNT_A, ACCOUNT_B);
    }
}
