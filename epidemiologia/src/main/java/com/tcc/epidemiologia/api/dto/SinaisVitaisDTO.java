package com.tcc.epidemiologia.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SinaisVitaisDTO(
        @NotNull @NotBlank Long id,
        Double temperatura,
        Double frequenciaRespiratoria,
        Double spo2,
        Double o2Percent,
        Double latitude,
        Double longitude,
        long timestamp
) {}
