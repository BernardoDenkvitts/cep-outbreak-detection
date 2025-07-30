package com.tcc.epidemiologia.service.drools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.springframework.stereotype.Component;

import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;

@Component
public abstract class AbstractSessionWorker extends Thread {

    protected final BlockingQueue<SinaisVitais> queue = new LinkedBlockingQueue<>();
    protected final StatefulKnowledgeSession session;

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
                System.out.println("Pegando da queue e inserindo no drools: " + sinaisVitais);
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
