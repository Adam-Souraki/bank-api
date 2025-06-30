package org.souraki.bankapi.domain.dto.account;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResponseDTO {
    private String accountNumber;
    private String firstName;
    private String lastName;
    private BigDecimal balance;
    private String currency = "DKK";
}
