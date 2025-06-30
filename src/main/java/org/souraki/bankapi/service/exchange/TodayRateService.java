package org.souraki.bankapi.service.exchange;

import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;

import java.math.BigDecimal;

public interface TodayRateService {
    ExchangeRateResponseDTO convertDKKToUSDAtTodayRate(BigDecimal amountDKK);

    HistoricalRateDTO getTodayDKKtoUSDHistoricalRate(BigDecimal amountDKK);
}
