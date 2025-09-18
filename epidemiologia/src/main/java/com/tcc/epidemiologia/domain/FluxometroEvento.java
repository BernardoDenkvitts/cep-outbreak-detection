package com.tcc.epidemiologia.domain;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;

import lombok.Data;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("11m")
@Data
public final class FluxometroEvento implements IEventoBase {
    private final EventoBase base;
    private final Double frequenciaRespiratoria;
    private final Double o2Percent;

    public FluxometroEvento(Long id, Double frequenciaRespiratoria, Double o2Percent, Long codigoBairro, long timestamp) {
        this.base = new EventoBase(id, codigoBairro, timestamp);
        this.frequenciaRespiratoria = frequenciaRespiratoria;
        this.o2Percent = o2Percent;
    }

    public static FluxometroEvento create(SinaisVitaisDTO dto, Long codigoBairro) {
        return new FluxometroEvento(
                dto.id(),
                dto.frequenciaRespiratoria(),
                dto.o2Percent(),
                codigoBairro,
                dto.timestamp());
    }

    @Override
    public Long getId() {
        return this.base.id();
    }

    @Override
    public Long getCodigoBairro() {
        return this.base.codigoBairro();
    }

    @Override
    public long getTimestamp() {
        return this.base.timestamp();
    }
}

