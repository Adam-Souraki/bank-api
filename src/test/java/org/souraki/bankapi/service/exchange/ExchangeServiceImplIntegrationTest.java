package org.souraki.bankapi.service.exchange;

import org.junit.jupiter.api.Test;
import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRatesResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExchangeServiceImplIntegrationTest {

    @Autowired
    private ExchangeService exchangeService;

    @MockitoBean
    private TodayRateService todayRateService;

    @MockitoBean
    private HistoricalRatesService historicalRatesService;

    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(100);

    @Test
    void testConvertDKKToUSDAtTodayRate() {
        when(todayRateService.convertDKKToUSDAtTodayRate(eq(TEST_AMOUNT)))
                .thenReturn(new ExchangeRateResponseDTO(TEST_AMOUNT, BigDecimal.valueOf(15.70)));

        ExchangeRateResponseDTO response = exchangeService.convertDKKToUSDAtTodayRate(TEST_AMOUNT);

        assertThat(response).isNotNull();
        assertThat(response.getDKK()).isEqualByComparingTo(TEST_AMOUNT);
        assertThat(response.getUSD()).isEqualByComparingTo(BigDecimal.valueOf(15.70));
    }

    @Test
    void testGetDKKtoUSDHistoricalAndTodayRates() {
        List<Integer> years = List.of(2005, 2006);

        List<HistoricalRateDTO> historicalRates = Arrays.asList(
                new HistoricalRateDTO("2005", BigDecimal.valueOf(0.1450), BigDecimal.valueOf(14.50)),
                new HistoricalRateDTO("2006", BigDecimal.valueOf(0.1460), BigDecimal.valueOf(14.60))
        );

        when(historicalRatesService.getDKKtoUSDHistoricalRates(eq(years), eq(TEST_AMOUNT)))
                .thenReturn(historicalRates);

        HistoricalRateDTO todayHistoricalRate = new HistoricalRateDTO("today",
                BigDecimal.valueOf(0.1570), BigDecimal.valueOf(15.70));
        when(todayRateService.getTodayDKKtoUSDHistoricalRate(eq(TEST_AMOUNT)))
                .thenReturn(todayHistoricalRate);


        HistoricalRatesResponseDTO response = exchangeService.getDKKtoUSDHistoricalAndTodayRates(years, TEST_AMOUNT);

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo(TEST_AMOUNT);
        assertThat(response.getRates()).hasSize(3);

        assertThat(response.getRates()).anyMatch(r -> r.getYear().equals("2005"));
        assertThat(response.getRates()).anyMatch(r -> r.getYear().equals("2006"));
        assertThat(response.getRates()).anyMatch(r -> r.getYear().equals("today"));
    }
}