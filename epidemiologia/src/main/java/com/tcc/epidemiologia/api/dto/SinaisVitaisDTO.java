package com.tcc.epidemiologia.api.dto;

public record SinaisVitaisDTO(
        Long id,
        Double temperatura,
        Double frequenciaRespiratoria,
        Double spo2,
        Double o2Percent,
        Double latitude,
        Double longitude,
        long timestamp
) {}
