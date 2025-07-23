package com.tcc.epidemiologia.api.dto;

public record SinaisVitaisDTO(
        Double temperatura,
        Integer pressaoSistolica,
        Integer pressaoDiastolica,
        Integer frequenciaCardiaca,
        Integer frequenciaRespiratoria,
        Integer spo2,
        Double latitude,
        Double longitude,
        long timestamp
) {}
