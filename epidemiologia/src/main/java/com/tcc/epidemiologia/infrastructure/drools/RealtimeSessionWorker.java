package com.tcc.epidemiologia.infrastructure.drools;

import com.tcc.epidemiologia.domain.IEventoBase;
import com.tcc.epidemiologia.infrastructure.EventCache;
import com.tcc.epidemiologia.service.BairroService;

import org.kie.api.KieBase;

public class RealtimeSessionWorker extends AbstractSessionWorker {

    public RealtimeSessionWorker(long codigoBairro, KieBase kieBase, BairroService bairroService, EventCache eventCache, Integer minimoCasos) {
        super(codigoBairro, kieBase, bairroService, eventCache, "realtime", minimoCasos);
    }

    @Override
    public void insere(IEventoBase evento) {
        queue.offer(evento);
    }
}
