package com.tcc.epidemiologia.infrastructure.drools;

import com.tcc.epidemiologia.domain.EventoClinico;
import com.tcc.epidemiologia.domain.IEventoBase;
import com.tcc.epidemiologia.infrastructure.EventCache;
import com.tcc.epidemiologia.repository.EventoClinicoRepository;
import com.tcc.epidemiologia.service.BairroService;

import org.kie.api.KieBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DroolsShardManager {

    private final EventoClinicoRepository repository;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${drools.retention.days}")
    private int retentionDays;

    private final Map<Long, AbstractSessionWorker> shards = new ConcurrentHashMap<>();

    public DroolsShardManager(
            KieBase kieBase,
            BairroService bairroService,
            EventCache eventCache,
            EventoClinicoRepository repository,
            @Value("${spring.profiles.active}") String profile,
            @Value("${valor-minimo-casos}") Integer minimoCasos) {

        this.repository = repository;
        this.profile = profile;

        for (Long codigo : bairroService.getAllCodigos()) {
            AbstractSessionWorker worker = profile.equalsIgnoreCase("dev")
                    ? new PseudoSessionWorker(codigo, kieBase, bairroService, eventCache, minimoCasos)
                    : new RealtimeSessionWorker(codigo, kieBase, bairroService, eventCache, minimoCasos);
            shards.put(codigo, worker);
        }
    }

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    @Transactional(readOnly = true)
    public void warmupAndStart() {
        if (!"prod".equalsIgnoreCase(profile)) {
            shards.values().forEach(Thread::start);
            return;
        }

        long since = System.currentTimeMillis() - retentionDays * 24L * 60L * 60L * 1000L;
        for (Long codigo : shards.keySet()) {
            var eventos = repository.findEventsByCodigoBairro(codigo, since);
            shards.get(codigo).bootstrapEventosClinicos(eventos);
        }

        shards.values().forEach(Thread::start);
    }

    public void submitEvent(IEventoBase evento) {
        AbstractSessionWorker worker = shards.get(evento.getCodigoBairro());
        worker.insere(evento);
    }

    public Map<Long, List<EventoClinico>> getEventosClinicosPorBairroWithinLastWindow(long windowMillis) {
        Map<Long, List<EventoClinico>> eventosPorBairro = new HashMap<>();
        shards.forEach((codigo, worker) -> {
            List<EventoClinico> eventos = worker.getEventosClinicosWithinLastWindow(windowMillis);
            if (!eventos.isEmpty()) {
                eventosPorBairro.put(codigo, eventos);
                for (EventoClinico eventoClinico : eventos) {
                    System.out.println("Evento na memoria do drools " + eventoClinico.getTipo());
                }
            }
        });
        return eventosPorBairro;
    }

    public Map<Long, Integer> getTotalEventosPorBairro() {
        Map<Long, Integer> eventosPorBairro = new HashMap<>();
        shards.forEach((codigo, worker) -> {
            Integer totalEventos = worker.getTotalEventosPorBairro();
            if (totalEventos > 0)
                eventosPorBairro.put(codigo, totalEventos);
        });
        return eventosPorBairro;
    }
}
