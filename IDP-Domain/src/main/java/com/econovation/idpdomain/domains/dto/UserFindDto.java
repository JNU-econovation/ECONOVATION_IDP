package com.econovation.idpdomain.domains.dto;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFindDto {
    @Min(0)
    @Max(100)
    private Integer year;

    private String userName;
}
