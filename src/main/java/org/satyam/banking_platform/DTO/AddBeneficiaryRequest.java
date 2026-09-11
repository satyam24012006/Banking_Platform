package org.satyam.banking_platform.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBeneficiaryRequest {
    private String name;
    private String accountNumber;
    private String bankName;
}
