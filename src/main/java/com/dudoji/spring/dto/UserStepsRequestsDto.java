package com.dudoji.spring.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
/*
  This class is used to transfer user step from Front to Back
  Multiple user step entries are allowed for flexibility and future scalability.
 */
public class UserStepsRequestsDto {
    List<UserStepDto> userSteps;

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (UserStepDto userStepDto : userSteps) {
            builder.append("{").append(userStepDto.toString()).append("} ");
        }
        return builder.toString();
    }

    @Getter
    @Setter
    public static class UserStepDto {
        private int step_count;
        private LocalDate step_date;

        @Override
        public String toString() {
            return String.format(step_date.toString(), step_count);
        }
    }
}
