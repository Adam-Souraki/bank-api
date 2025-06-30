package org.souraki.bankapi.domain.dto.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class HistoricalRateDTO {
    String year;
    BigDecimal rate;
    BigDecimal convertedAmount;
}
