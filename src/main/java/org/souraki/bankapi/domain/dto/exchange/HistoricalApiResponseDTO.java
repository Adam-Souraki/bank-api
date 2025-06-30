package org.souraki.bankapi.domain.dto.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class HistoricalApiResponseDTO {
    private String result;

    @JsonProperty("conversion_amounts")
    private Map<String, BigDecimal> conversionAmounts;
}
