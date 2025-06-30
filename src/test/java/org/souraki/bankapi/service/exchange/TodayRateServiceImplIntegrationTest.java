package org.souraki.bankapi.service.exchange;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.souraki.bankapi.domain.dto.exchange.ExchangeRateResponseDTO;
import org.souraki.bankapi.domain.dto.exchange.HistoricalRateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {TodayRateServiceImpl.class, TodayRateServiceImplIntegrationTest.TestExchangeConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
class TodayRateServiceImplIntegrationTest {

    private static ClientAndServer mockServer;

    @Configuration
    static class TestExchangeConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @Autowired
    private TodayRateService todayRateService;

    private String successJson;

    @BeforeAll
    static void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(9999);
    }

    @AfterAll
    static void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @BeforeEach
    void setupMockResponses() throws Exception {
        successJson = Files.readString(
                Paths.get("src/test/resources/mock/success-pair-response.json")
        );

        mockServer
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/dummy-key/pair/DKK/USD"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(successJson));
    }

    @Test
    void testConvertDKKToUSDAtTodayRate() {
        ExchangeRateResponseDTO result = todayRateService.convertDKKToUSDAtTodayRate(BigDecimal.valueOf(100));

        assertThat(result).isNotNull();
        assertThat(result.getDKK()).isEqualByComparingTo(BigDecimal.valueOf(100.0000));
        assertThat(result.getUSD()).isEqualByComparingTo(BigDecimal.valueOf(14.5600));
    }

    @Test
    void testGetTodayDKKtoUSDHistoricalRate() {
        HistoricalRateDTO result = todayRateService.getTodayDKKtoUSDHistoricalRate(BigDecimal.valueOf(100));

        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo("today");
        assertThat(result.getRate()).isEqualByComparingTo(BigDecimal.valueOf(0.1456));
        assertThat(result.getConvertedAmount()).isEqualByComparingTo(BigDecimal.valueOf(14.5600));
    }
}