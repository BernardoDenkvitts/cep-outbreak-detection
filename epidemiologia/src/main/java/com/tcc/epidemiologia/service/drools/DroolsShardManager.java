package com.tcc.epidemiologia.service.drools;

import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;

import org.kie.api.KieBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DroolsShardManager {

    private final Map<Long, AbstractSessionWorker> shards = new ConcurrentHashMap<>();

    public DroolsShardManager(KieBase kieBase, BairroService bairroService, @Value("${spring.profiles.active}") String profile, @Value("${valor-minimo-casos}") Integer minimoCasos) {
        List<Long> codigos = bairroService.getAllCodigos();
        codigos.forEach(codigo -> {
            AbstractSessionWorker worker = profile.equals("dev")
                    ? new PseudoSessionWorker(codigo, kieBase, bairroService, minimoCasos)
                    : new RealtimeSessionWorker(codigo, kieBase, bairroService, minimoCasos);
            shards.put(codigo, worker);
            worker.start();
        });
    }

    public void submitEvent(SinaisVitais evento) {
        AbstractSessionWorker worker = shards.get(evento.codigoBairro());
        worker.insere(evento);
    }

}
