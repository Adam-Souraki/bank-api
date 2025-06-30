package org.souraki.bankapi.domain.dto.account;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAccountRequestDTO {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "First name number is required")
    private String firstName;

    @NotBlank(message = "Last name number is required")
    private String lastName;

}
