package org.souraki.bankapi.domain.mapper;

import org.souraki.bankapi.domain.dto.account.AccountResponseDTO;
import org.souraki.bankapi.domain.entity.Account;

public class AccountMapper {
    public static AccountResponseDTO toDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setAccountNumber(account.getAccountNumber());
        dto.setFirstName(account.getFirstName());
        dto.setLastName(account.getLastName());
        dto.setBalance(account.getBalance());
        return dto;
    }
}
