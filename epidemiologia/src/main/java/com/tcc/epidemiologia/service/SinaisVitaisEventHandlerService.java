package com.tcc.epidemiologia.service;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;
import com.tcc.epidemiologia.domain.SinaisVitais;
import org.springframework.stereotype.Service;

@Service
public class SinaisVitaisEventHandlerService {

    private final BairroService bairroService;
    private final DroolsService droolsService;

    public SinaisVitaisEventHandlerService(BairroService bairroService, DroolsService droolsService) {
        this.bairroService = bairroService;
        this.droolsService = droolsService;
    }

    /**
     * Insere eventos (sinais vitais) na engine.
     */
    public void processar(SinaisVitaisDTO dto) {
        BairroService.Bairro bairro = bairroService.buscar(dto.latitude(), dto.longitude());
        if (bairro == null)
            throw new CoordenadasInvalidaException();

        droolsService.insert(SinaisVitais.create(dto, bairro.getCodigo()));
    }
}
