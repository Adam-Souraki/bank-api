package org.souraki.bankapi.service.exchange;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {HistoricalRatesServiceImpl.class, HistoricalRatesServiceImplIntegrationTest.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
class HistoricalRatesServiceImplIntegrationTest {

    @Configuration
    static class TestConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    private static ClientAndServer mockServer;

    @Autowired
    private HistoricalRatesService historicalRatesService;

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
    void setupMockServer() throws Exception {
        successJson = Files.readString(
                Paths.get("src/test/resources/mock/success-history-response.json")
        );

        mockServer
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/dummy-key/history/DKK/2005/1/1/100"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(successJson));
    }

    @Test
    void testGetDKKtoUSDHistoricalRates() {
        List<Integer> years = List.of(2005);
        BigDecimal amountDKK = BigDecimal.valueOf(100);

        List<HistoricalRateDTO> result = historicalRatesService.getDKKtoUSDHistoricalRates(years, amountDKK);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getYear()).isEqualTo("2005");
        assertThat(result.get(0).getConvertedAmount()).isEqualByComparingTo(BigDecimal.valueOf(14.5600));
    }
}
