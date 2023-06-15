package com.econovation.idpdomain.domains.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NonAccountResponseDto {
    private Integer year;
    private String name;
    private Long id;
}
