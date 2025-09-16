package com.tcc.epidemiologia.domain;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Id;

@Entity
@Table(name = "alertas")
@Role(Role.Type.EVENT)
@Timestamp("horarioAlerta")
@Expires("3h") // Novo email é enviado a cada 3 horas caso novos possíveis casos sejam recebidos
@Data
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "horario_alerta")
    private long horarioAlerta;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "bairro_codigo")
    private long bairroCodigo;

    @Column(name = "qtd_casos_semanais")
    private String qtdCasosSemanais;

    protected Alerta() {
    }

    public Alerta(Long id,
                  long horarioAlerta,
                  String tipo,
                  String bairro,
                  long bairroCodigo,
                  String qtdCasosSemanais) {
        this.id = id;
        this.horarioAlerta = horarioAlerta;
        this.tipo = tipo;
        this.bairro = bairro;
        this.bairroCodigo = bairroCodigo;
        this.qtdCasosSemanais = qtdCasosSemanais;
    }

    public static Alerta create(
            long horarioAlerta,
            String tipo,
            String bairro,
            long bairroCodigo,
            Integer qtdCasosSemAtual,
            Integer qtdCasosSemAnterior,
            Integer qtdCasosSemRetrasada,
            Integer qtdCasosSemAntepassada) {

        String texto = String.format(
            "Quantidade casos semana atual (0-7 dias): %d, " +
            "Quantidade casos semana passada (7-14 dias) atras: %d, " +
            "Quantidade casos semana retrasada (14-21 dias) atras: %d, " +
            "Quantidade casos semana antepassada (21-28 dias) atras: %d",
            qtdCasosSemAtual, qtdCasosSemAnterior, qtdCasosSemRetrasada, qtdCasosSemAntepassada
        );

        return new Alerta(
            null,
            horarioAlerta,
            tipo,
            bairro,
            bairroCodigo,
            texto
        );
    }

}