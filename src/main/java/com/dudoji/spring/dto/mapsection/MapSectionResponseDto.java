package com.dudoji.spring.dto.mapsection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapSectionResponseDto {
    public List<MapSectionDto> mapSections = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (MapSectionDto mapSectionDto : mapSections) {
            builder.append("{").append(mapSectionDto.toString()).append("} ");
        }
        return builder.toString();
    }


    @Getter
    @Setter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MapSectionDto {
        private int x;
        private int y;
        private boolean explored;

        @JsonProperty("mapData")
        private String base64Encoded;

        @Override
        public String toString() {
            return String.format("x: %d, y: %d, explored: %b\n encoded: %s", x, y, explored, base64Encoded);
        }
    }
}
