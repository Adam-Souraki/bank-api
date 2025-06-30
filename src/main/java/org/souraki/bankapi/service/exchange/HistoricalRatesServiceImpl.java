package org.souraki.bankapi.service.exchange;

import lombok.extern.slf4j.Slf4j;
import org.souraki.bankapi.domain.dto.exchange.HistoricalApiResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class HistoricalRatesServiceImpl implements HistoricalRatesService {

    private final WebClient webClient;

    @Value("${exchange.api.url}")
    private String apiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;


    public HistoricalRatesServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public List<HistoricalRateDTO> getDKKtoUSDHistoricalRates(List<Integer> years, BigDecimal amountDKK) {
        if (years == null || years.isEmpty()) {
            throw new IllegalArgumentException("Years list must not be null or empty");
        }

        if (amountDKK == null || amountDKK.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base amount must be positive");
        }

        Flux<HistoricalRateDTO> historicalFlux = Flux.fromIterable(years)
                .flatMap(year -> getHistoricalExchangeRateForYear(year, amountDKK))
                .onErrorContinue((ex, year) ->
                        log.warn("Error fetching year {}: {}", year, ex.getMessage()));

        return historicalFlux.collectList().block();
    }

    private Mono<HistoricalRateDTO> getHistoricalExchangeRateForYear(int year, BigDecimal amountDKK) {
        String requestUrl = String.format("%s/%s/history/DKK/%d/1/1/%s", apiUrl, apiKey, year, amountDKK);
        log.debug("Fetching historical rate for year {}: {}", year, requestUrl);

        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(HistoricalApiResponseDTO.class)
                .map(resp -> {
                    validateResponse(resp);
                    BigDecimal amountUSD = resp.getConversionAmounts().get("USD");
                    if (amountUSD == null) {
                        throw new IllegalStateException("Invalid USD rate in response for year " + year);
                    }
                    BigDecimal converted = amountUSD.setScale(4, RoundingMode.HALF_UP);
                    return new HistoricalRateDTO(
                            String.valueOf(year),
                            converted.divide(amountDKK, 4, RoundingMode.HALF_UP),
                            converted
                    );
                });
    }

    private void validateResponse(HistoricalApiResponseDTO response) {
        if (response == null || !"success".equalsIgnoreCase(response.getResult())) {
            throw new IllegalStateException("Failed to get exchange rate from provider");
        }
    }

}
