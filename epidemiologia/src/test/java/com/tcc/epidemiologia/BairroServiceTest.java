package com.tcc.epidemiologia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tcc.epidemiologia.service.BairroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BairroServiceTest {

    @Autowired
    private BairroService bairroService;

    @Test
    public void shouldReturnBairrosCorretos() {
        BairroService.Bairro bairroBoqueirao = bairroService.buscar(-28.26655, -52.42867);
        BairroService.Bairro bairroVilaSantaMaria = bairroService.buscar(-28.267204, -52.384626);
        BairroService.Bairro bairroPlanaltina = bairroService.buscar(-28.27785, -52.38835);
        BairroService.Bairro bairroPetropolis = bairroService.buscar(-28.24359, -52.38946);
        BairroService.Bairro bairroCentro = bairroService.buscar(-28.25797, -52.40659);
        BairroService.Bairro bairroValinhos = bairroService.buscar(-28.23771, -52.44255);

        assertEquals("Região do Bairro Boqueirão", bairroBoqueirao.getNome());
        assertEquals("Região do Bairro Vila Santa Maria", bairroVilaSantaMaria.getNome());
        assertEquals("Região do Bairro Planaltina", bairroPlanaltina.getNome());
        assertEquals("Região do Bairro Petrópolis", bairroPetropolis.getNome());
        assertEquals("Região do Bairro Centro (Centro e Vila Vergueiro)", bairroCentro.getNome());
        assertEquals("Região do Bairro Valinhos Loteamento Industrial", bairroValinhos.getNome());

    }
}
