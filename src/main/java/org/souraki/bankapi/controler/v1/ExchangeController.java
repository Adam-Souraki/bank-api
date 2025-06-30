package org.souraki.bankapi.controler.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRatesResponseDTO;
import org.souraki.bankapi.service.exchange.ExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/exchange")
@Tag(name = "Exchange", description = "Currency exchange operations")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Operation(summary = "Convert DKK to USD", description = "Uses a real 3rd-party provider to calculate the USD equivalent of the given DKK amount.")
    @GetMapping("/dkk-to-usd/conversion")
    public ResponseEntity<ExchangeRateResponseDTO> getExchangeRateDKKtoUSD(@RequestParam BigDecimal amount) {
        return ResponseEntity.ok(exchangeService.convertDKKToUSDAtTodayRate(amount));
    }

    @Operation(summary = "Get historical plus today's DKK to USD conversion rates for 100 DKK",
               description = "Returns conversion rates for 100 DKK from 2005-2015 (excluding 2012) plus today’s rate.")
    @GetMapping("/dkk-to-usd/historical-and-today")
    public ResponseEntity<HistoricalRatesResponseDTO> getDKKtoUSDHistoricalAndTodayRates() {
        BigDecimal amount = BigDecimal.valueOf(100);
        List<Integer> years = IntStream.rangeClosed(2005, 2015)
                .filter(y -> y != 2012)
                .boxed()
                .toList();

        HistoricalRatesResponseDTO response = exchangeService.getDKKtoUSDHistoricalAndTodayRates(years, amount);
        return ResponseEntity.ok(response);
    }

}
