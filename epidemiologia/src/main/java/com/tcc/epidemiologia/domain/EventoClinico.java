package com.tcc.epidemiologia.domain;

public record EventoClinico(
        String tipo,
        long codigoBairro,
        long timestamp
) { }
