package com.tcc.epidemiologia.infrastructure.drools;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieBase;

import com.tcc.epidemiologia.domain.IEventoBase;
import com.tcc.epidemiologia.infrastructure.EventCache;
import com.tcc.epidemiologia.service.BairroService;

/**
 * PseudoSessionWorker simula o avanço do tempo para testes em ambiente de
 * desenvolvimento.
 * Ele avança o relógio do Drools conforme os eventos são inseridos, permitindo
 * simular
 * o processamento de eventos com timestamps específicos.
 */
public class PseudoSessionWorker extends AbstractSessionWorker {

    private SessionPseudoClock clock;

    public PseudoSessionWorker(long codigoBairro, KieBase kieBase, BairroService bairroService, EventCache eventCache,
            Integer minimoCasos) {
        super(codigoBairro, kieBase, bairroService, eventCache, "pseudo", minimoCasos);
        this.clock = (SessionPseudoClock) this.session.getSessionClock();
        this.clock.advanceTime(1, TimeUnit.DAYS);

    }

    @Override
    public void insere(IEventoBase evento) {
        queue.offer(evento);

        long current = clock.getCurrentTime(); // onde o relogio está agora (ms desde o epoch)
        long target = evento.getTimestamp();
        long delta = target - current; // quanto precisamos avançar

        if (delta > 0) {
            clock.advanceTime(delta, TimeUnit.MILLISECONDS);
        }
    }
}
