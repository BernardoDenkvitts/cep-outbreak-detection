package com.tcc.epidemiologia.service.drools;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.springframework.stereotype.Component;

import com.tcc.epidemiologia.domain.EventoClinico;
import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;

@Component
public abstract class AbstractSessionWorker extends Thread {

    protected final BlockingQueue<SinaisVitais> queue = new LinkedBlockingQueue<>();
    protected final StatefulKnowledgeSession session;
    private static final Logger logger = LogManager.getLogger(AbstractSessionWorker.class);

    public AbstractSessionWorker(long codigoBairro, KieBase kieBase, BairroService bairroService, String clockType, Integer minimoCasos) {
        super("Drools-Bairro-" + codigoBairro);

        KieSessionConfiguration ksConf = KieServices.Factory.get().newKieSessionConfiguration();
        System.out.println("Clock Type: " + clockType);
        ksConf.setOption(ClockTypeOption.get(clockType));

        this.session = (StatefulKnowledgeSession) kieBase.newKieSession(ksConf, null);
        setDaemon(true);

        this.session.setGlobal("MINIMO_CASOS", minimoCasos);
        this.session.setGlobal("bairroService", bairroService);

        new Thread(session::fireUntilHalt, getName() + "-Firer").start();
    }

    public abstract void insere(SinaisVitais evento);

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                SinaisVitais sinaisVitais = queue.take();
                this.session.getEntryPoint("entrada-sinais-vitais").insert(sinaisVitais);
            }
        } catch (InterruptedException ex) {
            logger.error("[Thread " + getName() + "] ERRO : " + ex.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            session.dispose();
        }
    }

    public List<EventoClinico> getEventosClinicosWithinLastWindow(long windowMillis) {
        long agora = session.getSessionClock().getCurrentTime();
        long corte = agora - windowMillis;

        return session.getObjects(o -> o instanceof EventoClinico)
                .stream()
                .map(o -> (EventoClinico) o)
                .filter(evento -> evento.getTimestamp() >= corte)
                .collect(Collectors.toList());
    }
    
    public void bootstrapEventosClinicos(List<EventoClinico> eventosOrdenadosPorTimestamp) {
        if (eventosOrdenadosPorTimestamp.isEmpty())
            return;
        
        logger.info("Tamanho da lista " + eventosOrdenadosPorTimestamp.size());
        for (EventoClinico evento : eventosOrdenadosPorTimestamp) {
            logger.info("INSERINDO EVENTO -> " + evento.getCodigoBairro() + " " + evento.getTipo() + " " + new Date(evento.getTimestamp()));
            this.session.insert(evento);
        }
    }
}

