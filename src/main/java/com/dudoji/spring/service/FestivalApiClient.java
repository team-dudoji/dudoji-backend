package com.dudoji.spring.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.dudoji.spring.dto.festival.FestivalsResponseDto;
import com.dudoji.spring.models.domain.Festival;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FestivalApiClient {

    private final RestClient restClient;
    private final String festivalApiKey;

    protected FestivalApiClient(
            @Value("${open-api.festival.key}") String festivalApiKey,
            @Value("${open-api.festival.end-point}") String festivalApiEndPoint
    ) {
        this.restClient = RestClient
                .builder()
                .baseUrl(festivalApiEndPoint)
                .build();
        this.festivalApiKey = festivalApiKey;
    }

    public List<Festival> getFestivalData(LocalDate referenceDate, int numOfRows, int pageNo) {
        ResponseSpec spec = restClient.get()
                .uri(urlBuilder ->
                    urlBuilder.queryParam("type","json")
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("pageNo", pageNo)
                            .queryParam("referenceDate", referenceDate)
                            .queryParam("serviceKey", festivalApiKey)
                            .build()
                )
                .retrieve();

        try {
            ResponseEntity<FestivalsResponseDto> response = spec.toEntity(FestivalsResponseDto.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                return null;
            }

            return response
                    .getBody()
                    .toDomains();
        } catch (Exception e) {
            log.error("Fail to Fetch Festival Data", e);
            return null;
        }
    }
}
