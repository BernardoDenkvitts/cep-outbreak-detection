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
    String sinaisVitais
) {}
