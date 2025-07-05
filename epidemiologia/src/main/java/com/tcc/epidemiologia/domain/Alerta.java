package com.tcc.epidemiologia.domain;

import java.time.LocalDateTime;

public record Alerta (
        LocalDateTime timestamp,
        LocalDateTime horarioAlerta,
        String tipo,
        String bairro,
        String sinaisVitais
) {}
