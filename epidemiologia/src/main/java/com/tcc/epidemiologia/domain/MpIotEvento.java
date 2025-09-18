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
public final class MpIotEvento implements IEventoBase {
    private final EventoBase base;
    private Double temperatura;
    private Double spo2;

    public MpIotEvento(Long id, Double temperatura, Double spo2, Long codigoBairro, long timestamp) {
        this.base = new EventoBase(id, codigoBairro, timestamp);
        this.temperatura = temperatura;
        this.spo2 = spo2;
    }

    public static MpIotEvento create(SinaisVitaisDTO dto, Long codigoBairro) {
        return new MpIotEvento(
                dto.id(),
                dto.temperatura(),
                dto.spo2(),
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
