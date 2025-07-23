package com.tcc.epidemiologia.Rules;

import com.tcc.epidemiologia.domain.Alerta;
import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SragTest {

    private KieSession ksession;
    private SessionPseudoClock clock;
    private EntryPoint entrada;
    @Autowired
    private BairroService bairroService;

    @BeforeEach
    public void configurarSessaoDrools() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/rules.drl"));
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();

        Results results = kb.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            results.getMessages(Message.Level.ERROR).forEach(System.out::println);
            throw new IllegalStateException("Erro ao compilar regras Drools");
        }

        KieModule kieModule = kb.getKieModule();

        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
        KieBase kieBase = kieContainer.newKieBase(kieBaseConfiguration);

        KieSessionConfiguration ksConfig = kieServices.newKieSessionConfiguration();
        ksConfig.setOption(ClockTypeOption.get("pseudo"));

        this.ksession = kieBase.newKieSession(ksConfig, null);
        this.entrada = this.ksession.getEntryPoint("entrada-sinais-vitais");
        this.ksession.setGlobal("MINIMO_CASOS", 1200);
        this.ksession.setGlobal("bairroService", bairroService);

        this.clock = this.ksession.getSessionClock();
        clock.advanceTime(1, TimeUnit.DAYS);
        //TempoUtils.setClock(() -> this.clock.getCurrentTime());
    }

    private SinaisVitais novoSinalSRAG(long bairro, long timestamp) {
        return new SinaisVitais(38.5, null, null, 110, 32, 92, timestamp, bairro);
    }

    @Test
    public void deveCriarAlertaDeSurtoSRAG() {
        long codigoBairro = 43141000508L;
        int[] casosPorSemana = { 300, 400, 500, 600 };
        
        long timestamp = clock.getCurrentTime();

        for (int i = 0; i < casosPorSemana.length; i++) {
            for (int j = 0; j < casosPorSemana[i]; j++) {
                entrada.insert(novoSinalSRAG(codigoBairro, timestamp));
                ksession.fireAllRules();
            }
            System.out.printf("Semana %d: inseridos %d casos em %s%n",
                    i - (casosPorSemana.length - 1),
                    casosPorSemana[i],
                    new Date(timestamp));
            
            // So avança o relógio se não for a última semana (a atual)
            if (i < casosPorSemana.length - 1) {
                timestamp = clock.advanceTime(8, TimeUnit.DAYS);
            }
        }

        long totalAlertas = ksession.getObjects()
                .stream()
                .filter(o -> o instanceof Alerta)
                .count();

        assertEquals(1, totalAlertas, "Deve gerar um alerta de surto SRAG");
        
        Alerta alerta = ksession.getObjects()
                .stream()
                .filter(o -> o instanceof Alerta)
                .map(o -> (Alerta) o)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Alerta não encontrado"));
        System.out.println("Quantidade de casos : " + alerta.qtdCasosSemanais());
        ksession.dispose();
    }

}
