package com.tcc.epidemiologia.service;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;
import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.infrastructure.EventCache;
import com.tcc.epidemiologia.infrastructure.drools.DroolsShardManager;

import org.springframework.stereotype.Service;

@Service
public class SinaisVitaisEventHandlerService {

    private final BairroService bairroService;
    private final DroolsShardManager shardManager;
    private final EventCache eventCache;

    public SinaisVitaisEventHandlerService(BairroService bairroService, DroolsShardManager shardManager,
            EventCache eventCache) {
        this.bairroService = bairroService;
        this.shardManager = shardManager;
        this.eventCache = eventCache;
    }

    public void processar(SinaisVitaisDTO dto) {
        // Evita ter mais de um evento de possível SRAG para o mesmo id;
        if (eventCache.contains(dto.id())) {
            return;
        }

        BairroService.Bairro bairro = bairroService.buscar(dto.latitude(), dto.longitude());
        if (bairro == null) {
            throw new CoordenadasInvalidaException();
        }

        SinaisVitais evento = SinaisVitais.create(dto, bairro.getCodigo());
        shardManager.submitEvent(evento);
    }
}
