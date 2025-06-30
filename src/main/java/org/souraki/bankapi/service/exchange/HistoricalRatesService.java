package org.souraki.bankapi.service.exchange;

import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;

import java.math.BigDecimal;
import java.util.List;

public interface HistoricalRatesService {
    List<HistoricalRateDTO> getDKKtoUSDHistoricalRates(List<Integer> years, BigDecimal amountDKK);
}
