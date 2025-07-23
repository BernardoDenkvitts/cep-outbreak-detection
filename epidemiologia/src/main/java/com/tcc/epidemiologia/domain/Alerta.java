package com.tcc.epidemiologia.domain;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("horarioAlerta")
@Expires("7d")
public record Alerta (
    long horarioAlerta,
    String tipo,
    String bairro,
    long bairroCodigo,
    String qtdCasosSemanais
) {
    public static Alerta create(
        long horarioAlerta,
        String tipo,
        String bairro,
        long bairroCodigo,
        Integer qtdCasosSemAtual, Integer qtdCasosSemAnterior,
        Integer qtdCasosSemRetrasada, Integer qtdCasosSemAntepassada
    ) {
        String qtdCasosSemanais = String.format(
            "Quantidade casos semana atual (0-7 dias): %d, " +
            "Quantidade casos semana passada (7-14 dias) atras: %d, " +
            "Quantidade casos semana retrasada (14-21 dias) atras: %d, " +
            "Quantidade casos semana antepassada (21-28 dias) atras: %d",
            qtdCasosSemAtual, qtdCasosSemAnterior, qtdCasosSemRetrasada, qtdCasosSemAntepassada
        );
        return new Alerta(horarioAlerta, tipo, bairro, bairroCodigo, qtdCasosSemanais);
    }
}
