package com.tcc.epidemiologia.service.drools;

import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;
import org.kie.api.KieBase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DroolsShardManager {

    private final Map<Long, SessionWorker> shards = new ConcurrentHashMap<>();

    public DroolsShardManager(KieBase kieBase, BairroService bairroService) {
        List<Long> codigos = bairroService.getAllCodigos();
        codigos.forEach(codigo -> {
            SessionWorker worker = new SessionWorker(codigo, kieBase);
            shards.put(codigo, worker);
            worker.start();
        });
    }

    public void submitEvent(SinaisVitais evento) {
        long codigo = evento.codigoBairro();
        // Insere o evento no worker do respectivo bairro
        SessionWorker worker = shards.get(codigo);
        System.out.println("INSERINDO NA SHARD " + codigo);
        worker.insere(evento);
    }

}
