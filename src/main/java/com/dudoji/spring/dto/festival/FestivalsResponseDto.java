package com.dudoji.spring.dto.festival;

import java.time.LocalDate;
import java.util.List;

import com.dudoji.spring.dto.festival.FestivalsResponseDto.Response.Body.FestivalResponseDto;
import com.dudoji.spring.models.domain.Festival;

public record FestivalsResponseDto(
    Response response
) {

    public record Response(
        Body body
    ) {
        public record Body(
            List<FestivalResponseDto> items
        ) {
            public record FestivalResponseDto(
                    String fstvlNm,
                    String opar,
                    LocalDate fstvlStartDate,
                    LocalDate fstvlEndDate,
                    String fstvlCo,
                    String mnnstNm,
                    String auspcInsttNm,
                    String suprtInsttNm,
                    String phoneNumber,
                    String homepageUrl,
                    String relateInfo,
                    String rdnmadr,
                    String lnmadr,
                    Double latitude,
                    Double longitude,
                    LocalDate referenceDate,
                    String insttCode,
                    String insttNm
            ) {
                public Festival toDomain() {
                    return new Festival(fstvlNm, opar, fstvlStartDate, fstvlEndDate, fstvlCo, mnnstNm,
                            auspcInsttNm, suprtInsttNm, phoneNumber, homepageUrl, relateInfo, rdnmadr, lnmadr, latitude, longitude, referenceDate, insttCode, insttNm);
                }

            }
        }
    }

    public List<Festival> toDomains() {
        return response.body
                .items
                .stream()
                .map(FestivalResponseDto::toDomain)
                .toList();
    }
}
