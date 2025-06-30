package org.souraki.bankapi.domain.dto.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExchangeApiResponseDTO {
    private String result;

    @JsonProperty("conversion_rate")
    private double conversionRate;

}