package com.tcc.epidemiologia.Rules;

import com.tcc.epidemiologia.domain.Alerta;
import com.tcc.epidemiologia.domain.EventoClinico;
import com.tcc.epidemiologia.domain.SinaisVitais;
import com.tcc.epidemiologia.service.BairroService;
import com.tcc.epidemiologia.utils.TempoUtils;
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
        this.ksession.setGlobal("MINIMO_CASOS", 500);
        this.ksession.setGlobal("bairroService", bairroService);

        this.clock = this.ksession.getSessionClock();
        clock.advanceTime(1, TimeUnit.DAYS);
        TempoUtils.setClock(() -> this.clock.getCurrentTime());
    }

    private SinaisVitais novoSinalSRAG(long bairro, long timestamp) {
        return new SinaisVitais(
                38.5, null, null, 110, 32, 92, timestamp, bairro
        );
    }

    @Test
    public void deveManterEventosClinicosNaMemoria() {
        long bairro = 42;

        // Semana -4
        System.out.println("28 A 22 DIAS ATRAS");
        System.out.println("CURRENT DATE -> " + new Date(clock.getCurrentTime()));
        for (int i = 0; i < 2; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }
        ksession.fireAllRules();

        // Semana -3
        clock.advanceTime(7, TimeUnit.DAYS);
        System.out.println("21 A 15 DIAS ATRAS");
        System.out.println("CURRENT DATE -> " + new Date(clock.getCurrentTime()));
        for (int i = 0; i < 2; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }
        ksession.fireAllRules();

        // Semana -2
        clock.advanceTime(7, TimeUnit.DAYS); // agora estamos no dia 15
        System.out.println("14 A 8 DIAS ATRAS");
        System.out.println("CURRENT DATE -> " + new Date(clock.getCurrentTime()));
        for (int i = 0; i < 2; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }
        ksession.fireAllRules();

        // Semana -1
        clock.advanceTime(8, TimeUnit.DAYS);
        System.out.println("ULTIMOS 7 DIAS");
        System.out.println("CURRENT DATE -> " + new Date(clock.getCurrentTime()));
        for (int i = 0; i < 2; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }
        ksession.fireAllRules();

        long totalEventoClinico = ksession.getObjects()
                .stream()
                .filter(obj -> obj instanceof EventoClinico)
                .count();

        System.out.println("Total de EventoClinico na memória: " + totalEventoClinico);
        assertEquals(8, totalEventoClinico, "Todos os eventos clínicos devem estar na memória");
    }

    @Test
    public void deveDispararRegraDeSurtoDeSRAG() {
        long bairro = 43141000508L;

        // Semana -4 (28 a 22 dias atrás)
        for (int i = 0; i < 100; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }

        // Semana -3 (21 a 15 dias atrás)
        clock.advanceTime(7, TimeUnit.DAYS);
        for (int i = 0; i < 100; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }

        // Semana -2 (14 a 8 dias atrás)
        clock.advanceTime(7, TimeUnit.DAYS);
        for (int i = 0; i < 350; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }

        // Semana atual (últimos 7 dias)
        clock.advanceTime(7, TimeUnit.DAYS);
        for (int i = 0; i < 1000; i++) {
            entrada.insert(novoSinalSRAG(bairro, clock.getCurrentTime()));
        }

        clock.advanceTime(6, TimeUnit.DAYS);
        ksession.fireAllRules();

        long alertaGerado = ksession.getObjects()
                .stream()
                .filter(obj -> obj instanceof Alerta)
                .count();

        assertEquals(1, alertaGerado, "Alerta devia ter sido gerado");
    }

}
