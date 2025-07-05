package com.tcc.epidemiologia.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;

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
