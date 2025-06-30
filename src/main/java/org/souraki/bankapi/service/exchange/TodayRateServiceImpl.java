package org.souraki.bankapi.service.exchange;


import lombok.extern.slf4j.Slf4j;
import org.souraki.bankapi.domain.dto.exchange.ExchangeApiResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class TodayRateServiceImpl implements TodayRateService {

    private final WebClient webClient;

    @Value("${exchange.api.url}")
    private String apiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;

    public TodayRateServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public ExchangeRateResponseDTO convertDKKToUSDAtTodayRate(BigDecimal amountDKK) {
        BigDecimal rate = getTodayExchangeRateFromPairApi().block();
        BigDecimal amountUSD = amountDKK.multiply(rate).setScale(4, RoundingMode.HALF_UP);

        log.info("Converted {} DKK to {} USD using rate {}", amountDKK, amountUSD, rate);
        return new ExchangeRateResponseDTO(amountDKK.setScale(4, RoundingMode.HALF_UP), amountUSD);
    }

    @Override
    public HistoricalRateDTO getTodayDKKtoUSDHistoricalRate(BigDecimal amountDKK) {
        BigDecimal rate = getTodayExchangeRateFromPairApi().block();
        return new HistoricalRateDTO(
                "today",
                rate != null ? rate.setScale(4, RoundingMode.HALF_UP) : null,
                amountDKK.multiply(rate).setScale(4, RoundingMode.HALF_UP)
        );
    }

    private Mono<BigDecimal> getTodayExchangeRateFromPairApi() {
        String requestUrl = String.format("%s/%s/pair/DKK/USD", apiUrl, apiKey);
        log.debug("Calling external API: {}", requestUrl);

        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(ExchangeApiResponseDTO.class)
                .map(resp -> {
                    validateResponse(resp);
                    return BigDecimal.valueOf(resp.getConversionRate());
                });
    }

    private void validateResponse(ExchangeApiResponseDTO response) {
        if (response == null || !"success".equalsIgnoreCase(response.getResult())) {
            throw new IllegalStateException("Failed to get exchange rate from provider");
        }
    }
}
