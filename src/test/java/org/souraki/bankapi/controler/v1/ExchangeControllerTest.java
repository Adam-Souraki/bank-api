package org.souraki.bankapi.controler.v1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRatesResponseDTO;
import org.souraki.bankapi.service.exchange.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExchangeControllerTest {

    @Mock
    private ExchangeService exchangeService;

    @InjectMocks
    private ExchangeController exchangeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeRateDKKtoUSD() {
        BigDecimal amountDKK = BigDecimal.valueOf(100);
        BigDecimal amountUSD = BigDecimal.valueOf(14.56);
        ExchangeRateResponseDTO expectedResponse = new ExchangeRateResponseDTO(amountDKK, amountUSD);
        when(exchangeService.convertDKKToUSDAtTodayRate(amountDKK)).thenReturn(expectedResponse);

        ResponseEntity<ExchangeRateResponseDTO> response = exchangeController.getExchangeRateDKKtoUSD(amountDKK);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(exchangeService).convertDKKToUSDAtTodayRate(amountDKK);
    }

    @Test
    void testGetDKKtoUSDHistoricalAndTodayRates() {
        BigDecimal amountDKK = BigDecimal.valueOf(100);
        List<Integer> years = List.of(2005, 2006);
        List<HistoricalRateDTO> entries = List.of(
                new HistoricalRateDTO("2005", BigDecimal.valueOf(0.1450), BigDecimal.valueOf(14.50)),
                new HistoricalRateDTO("2006", BigDecimal.valueOf(0.1460), BigDecimal.valueOf(14.60)),
                new HistoricalRateDTO("today", BigDecimal.valueOf(0.1570), BigDecimal.valueOf(15.70))
        );
        HistoricalRatesResponseDTO expectedResponse = new HistoricalRatesResponseDTO(amountDKK, entries);
        when(exchangeService.getDKKtoUSDHistoricalAndTodayRates(anyList(), eq(amountDKK))).thenReturn(expectedResponse);

        ResponseEntity<HistoricalRatesResponseDTO> response = exchangeController.getDKKtoUSDHistoricalAndTodayRates();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(exchangeService).getDKKtoUSDHistoricalAndTodayRates(anyList(), eq(amountDKK));
    }

}
