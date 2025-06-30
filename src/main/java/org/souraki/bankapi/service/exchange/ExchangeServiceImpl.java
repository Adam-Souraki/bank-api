package org.souraki.bankapi.service.exchange;

import lombok.extern.slf4j.Slf4j;
import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRatesResponseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ExchangeServiceImpl implements ExchangeService {

    private final TodayRateService todayRateService;
    private final HistoricalRatesService historicalRatesService;

    public ExchangeServiceImpl(TodayRateService todayRateService, HistoricalRatesService historicalRatesService) {
        this.todayRateService = todayRateService;
        this.historicalRatesService = historicalRatesService;
    }

    @Override
    public ExchangeRateResponseDTO convertDKKToUSDAtTodayRate(BigDecimal amountDKK) {
        return todayRateService.convertDKKToUSDAtTodayRate(amountDKK);
    }

    @Override
    public HistoricalRatesResponseDTO getDKKtoUSDHistoricalAndTodayRates(List<Integer> years, BigDecimal amountDKK) {
        List<HistoricalRateDTO> historicalRates = historicalRatesService.getDKKtoUSDHistoricalRates(years, amountDKK);

        Mono<HistoricalRateDTO> todayMono = Mono.fromCallable(() -> todayRateService.getTodayDKKtoUSDHistoricalRate(amountDKK))
                .onErrorResume(ex -> {
                    log.warn("Error fetching today's rate: {}", ex.getMessage());
                    return Mono.empty();
                });

        List<HistoricalRateDTO> allRates = Flux.concat(Flux.fromIterable(historicalRates), todayMono).collectList().block();

        return new HistoricalRatesResponseDTO(amountDKK, allRates);
    }

}
