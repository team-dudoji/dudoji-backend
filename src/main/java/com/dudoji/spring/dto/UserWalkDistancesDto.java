package com.dudoji.spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
/*
  This class is used to transfer user step from Front to Back
  Multiple user step entries are allowed for flexibility and future scalability.
 */
public class UserWalkDistancesDto {
    @JsonProperty("distances")
    public List<UserWalkDistanceDto> userWalkDistances;

    public UserWalkDistancesDto() {
        this.userWalkDistances = new ArrayList<>();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (UserWalkDistanceDto userWalkDistanceDto : userWalkDistances) {
            builder.append("{").append(userWalkDistanceDto.toString()).append("} ");
        }
        return builder.toString();
    }

    @Getter
    @Setter
    @Builder
    public static class UserWalkDistanceDto {
        private int distance;
        private LocalDate date;

        @Override
        public String toString() {
            return String.format(date.toString(), distance);
        }
    }
}
