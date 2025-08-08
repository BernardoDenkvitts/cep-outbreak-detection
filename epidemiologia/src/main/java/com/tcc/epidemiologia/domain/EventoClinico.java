package com.tcc.epidemiologia.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "eventos_clinicos")
@Data
public class EventoClinico {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "evento_seq"
    )
    @SequenceGenerator(
        name = "evento_seq",
        sequenceName = "eventos_clinicos_id_seq",
        allocationSize = 100
    )
    private Long id;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "codigo_bairro")
    private long codigoBairro;

    @Column(name = "timestamp")
    private long timestamp;

    protected EventoClinico() {
    }

    public EventoClinico(Long id, String tipo, long codigoBairro, long timestamp) {
        this.id = id;
        this.tipo = tipo;
        this.codigoBairro = codigoBairro;
        this.timestamp = timestamp;
    }

    public static EventoClinico create(String tipo, long codigoBairro, long timestamp) {
        return new EventoClinico(null, tipo, codigoBairro, timestamp);
    }
}
