package com.tcc.epidemiologia.domain;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;
import com.tcc.epidemiologia.service.BairroService;

import java.time.Instant;

public record SinaisVitais(
        Long userId,
        Double temperatura,
        Integer pressaoSistolica,
        Integer pressaoDiastolica,
        Integer frequenciaCardiaca,
        Integer frequenciaRespiratoria,
        Integer spo2,
        Instant timestamp,
        long codigoBairro
) {

    public static SinaisVitais create(SinaisVitaisDTO dto, long codigoBairro) {
        return new SinaisVitais(
                dto.userId(),
                dto.temperatura(),
                dto.pressaoSistolica(),
                dto.pressaoDiastolica(),
                dto.frequenciaCardiaca(),
                dto.frequenciaRespiratoria(),
                dto.spo2(),
                Instant.parse(dto.timestamp()),
                codigoBairro
        );
    }

}
