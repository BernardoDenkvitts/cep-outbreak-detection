package com.tcc.epidemiologia.domain;

public sealed interface IEventoBase permits MpIotEvento, FluxometroEvento {
    Long getId();

    Long getCodigoBairro();

    long getTimestamp();
}
