package com.tcc.epidemiologia.domain;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;

public record SinaisVitais(
        Double temperatura,
        Integer pressaoSistolica,
        Integer pressaoDiastolica,
        Integer frequenciaCardiaca,
        Integer frequenciaRespiratoria,
        Integer spo2,
        long timestamp,
        long codigoBairro
) {

    public static SinaisVitais create(SinaisVitaisDTO dto, long codigoBairro) {
        return new SinaisVitais(
                dto.temperatura(),
                dto.pressaoSistolica(),
                dto.pressaoDiastolica(),
                dto.frequenciaCardiaca(),
                dto.frequenciaRespiratoria(),
                dto.spo2(),
                dto.timestamp(),
                codigoBairro
        );
    }

}
