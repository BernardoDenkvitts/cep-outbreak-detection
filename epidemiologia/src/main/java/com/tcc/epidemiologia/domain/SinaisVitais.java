package com.tcc.epidemiologia.domain;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;

public record SinaisVitais(
        Long id,
        Double temperatura,
        Double frequenciaRespiratoria,
        Double spo2,
        Double o2Percent,
        long timestamp,
        long codigoBairro
) {

    public static SinaisVitais create(SinaisVitaisDTO dto, long codigoBairro) {
        return new SinaisVitais(
                dto.id(),
                dto.temperatura(),
                dto.frequenciaRespiratoria(),
                dto.spo2(),
                dto.o2Percent(),
                dto.timestamp(),
                codigoBairro
        );
    }

}
