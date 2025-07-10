package com.tcc.epidemiologia.service.drools;

import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionWorker extends Thread {

    private final BlockingQueue<SinaisVitais> queue = new LinkedBlockingQueue<>();
    private final StatefulKnowledgeSession session;
    @Value("valor-minimo-casos")
    private Integer minimoCasos;

    public SessionWorker(long codigoBairro, KieBase kieBase, BairroService bairroService) {
        super("Drools-Bairro-" + codigoBairro);
        KieSessionConfiguration ksConf = KieServices.Factory.get().newKieSessionConfiguration();
        ksConf.setOption(ClockTypeOption.get("realtime"));

        this.session = (StatefulKnowledgeSession) kieBase.newKieSession(ksConf, null);
        setDaemon(true);
        this.session.setGlobal("MINIMO_CASOS", minimoCasos);
        this.session.setGlobal("bairroService", bairroService);

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
                this.session.getEntryPoint("entrada-sinais-vitais").insert(sinaisVitais);
            }
        } catch (InterruptedException ex) {
            System.out.println("ERRO -> " + ex.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            session.dispose();
        }
    }
}
