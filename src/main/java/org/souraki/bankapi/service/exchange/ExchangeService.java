package org.souraki.bankapi.service.exchange;

import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRatesResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeService {
    ExchangeRateResponseDTO convertDKKToUSDAtTodayRate(BigDecimal amountDKK);

    HistoricalRatesResponseDTO getDKKtoUSDHistoricalAndTodayRates(List<Integer> years, BigDecimal amountDKK);
}
