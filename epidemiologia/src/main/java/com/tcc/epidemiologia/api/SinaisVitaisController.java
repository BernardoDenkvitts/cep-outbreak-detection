package com.tcc.epidemiologia.api;

import com.tcc.epidemiologia.api.dto.SinaisVitaisDTO;
import com.tcc.epidemiologia.service.SinaisVitaisEventHandlerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SinaisVitaisController {

    private final SinaisVitaisEventHandlerService service;

    public SinaisVitaisController(SinaisVitaisEventHandlerService service) {
        this.service = service;
    }

    @PostMapping("/sinais-vitais")
    public ResponseEntity<String> recebeSinaisVitais(@RequestBody SinaisVitaisDTO dto) {
        service.processar(dto);
        return ResponseEntity.accepted().body("Recebido");
    }
}
