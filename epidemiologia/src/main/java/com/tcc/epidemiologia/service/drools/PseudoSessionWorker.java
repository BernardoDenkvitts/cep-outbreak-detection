package com.tcc.epidemiologia.service.drools;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieBase;

import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;

/**
 * PseudoSessionWorker simula o avanço do tempo para testes em ambiente de desenvolvimento.
 * Ele avança o relógio do Drools conforme os eventos são inseridos, permitindo simular
 * o processamento de eventos com timestamps específicos.
 */
public class PseudoSessionWorker extends AbstractSessionWorker {

    private SessionPseudoClock clock;

    public PseudoSessionWorker(long codigoBairro, KieBase kieBase, BairroService bairroService, Integer minimoCasos) {
        super(codigoBairro, kieBase, bairroService, "pseudo", minimoCasos);
        this.clock = (SessionPseudoClock) this.session.getSessionClock();
        this.clock.advanceTime(1, TimeUnit.DAYS);

    }

    public void insere(SinaisVitais evento) {
        queue.offer(evento);

        long current = clock.getCurrentTime(); // onde o relogio está agora (ms desde o epoch)
        long target = evento.timestamp(); // timestamp do evento
        long delta = target - current; // quanto precisamos avançar

        if (delta > 0) {
            clock.advanceTime(delta, TimeUnit.MILLISECONDS);
            System.out.println("Tempo atual do clock: " + clock.getCurrentTime());
        }
    }
}
