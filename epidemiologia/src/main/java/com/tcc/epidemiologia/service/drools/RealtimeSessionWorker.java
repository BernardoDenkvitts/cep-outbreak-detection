package com.tcc.epidemiologia.service.drools;

import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;

import org.kie.api.KieBase;

public class RealtimeSessionWorker extends AbstractSessionWorker {

    public RealtimeSessionWorker(long codigoBairro, KieBase kieBase, BairroService bairroService, Integer minimoCasos) {
        super(codigoBairro, kieBase, bairroService, "realtime", minimoCasos);
    }

    public void insere(SinaisVitais evento) {
        queue.offer(evento);
    }
}
