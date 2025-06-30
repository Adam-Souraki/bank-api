package org.souraki.bankapi.domain.dto.exchange;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class HistoricalRatesResponseDTO {
    BigDecimal amount;
    List<HistoricalRateDTO> rates;
}
