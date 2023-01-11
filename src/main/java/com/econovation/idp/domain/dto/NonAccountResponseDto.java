package com.econovation.idp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NonAccountResponseDto {
    private Long year;
    private String name;
    private Long id;
}
