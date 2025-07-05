package com.tcc.epidemiologia.service.drools;

import com.tcc.epidemiologia.domain.SinaisVitais;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionWorker extends Thread {

    private final BlockingQueue<SinaisVitais> queue = new LinkedBlockingQueue<>();
    private final StatefulKnowledgeSession session;

    public SessionWorker(long codigoBairro, KieBase kieBase) {
        super("Drools-Bairro-" + codigoBairro);
        KieSessionConfiguration ksConf = KieServices.Factory.get().newKieSessionConfiguration();
        ksConf.setOption(ClockTypeOption.get("realtime"));

        this.session = (StatefulKnowledgeSession) kieBase.newKieSession(ksConf, null);
        setDaemon(true);
        new Thread(session::fireUntilHalt, getName() + "-Firer").start();
    }

    public void insere(SinaisVitais evento) {
        queue.offer(evento);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                SinaisVitais sinaisVitais = queue.take();
                session.insert(sinaisVitais);
            }
        } catch (InterruptedException ex) {
            System.out.println("ERRO -> " + ex.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            session.dispose();
        }
    }
}
