package com.tcc.epidemiologia.domain;

public record EventoBase(
    Long id,
    Long codigoBairro,
    long timestamp
) {}
