package com.tcc.epidemiologia.service;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;
import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.drools.DroolsShardManager;
import org.springframework.stereotype.Service;

@Service
public class SinaisVitaisEventHandlerService {

    private final BairroService bairroService;
    private final DroolsShardManager shardManager;

    public SinaisVitaisEventHandlerService(BairroService bairroService,
                                           DroolsShardManager shardManager) {
        this.bairroService = bairroService;
        this.shardManager = shardManager;
    }

    public void processar(SinaisVitaisDTO dto) {
        BairroService.Bairro bairro = bairroService.buscar(dto.latitude(), dto.longitude());
        if (bairro == null) {
            throw new CoordenadasInvalidaException();
        }
        // cria evento com codigoBairro
        SinaisVitais evento = SinaisVitais.create(dto, bairro.getCodigo());
        shardManager.submitEvent(evento);
    }
}
