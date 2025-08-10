package com.tcc.epidemiologia.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcc.epidemiologia.domain.EventoClinico;
import com.tcc.epidemiologia.repository.EventoClinicoRepository;
import com.tcc.epidemiologia.service.drools.DroolsShardManager;

@Service
public class EventosClinicosScheduledService {

    private final DroolsShardManager droolsShardManager;
    private final EventoClinicoRepository eventoClinicoRepository;
    @Value("${drools.retention.days}")
    private int retentionDays;
    private final Logger logger = LogManager.getLogger(EventosClinicosScheduledService.class);

    public EventosClinicosScheduledService(DroolsShardManager droolsShardManager,
            EventoClinicoRepository eventoClinicoRepository) {
        this.droolsShardManager = droolsShardManager;
        this.eventoClinicoRepository = eventoClinicoRepository;
    }

    @Scheduled(fixedRate = 10, initialDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void persisteEventosClinicos() {
        logger.info("Iniciando JOB para salvar Eventos Clinicos no banco");
        long window = Duration.ofMinutes(10).toMillis();

        Map<Long, List<EventoClinico>> eventosPorBairro = droolsShardManager
                .getEventosClinicosPorBairroWithinLastWindow(window);

        List<EventoClinico> eventosList = eventosPorBairro.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Se tiver mais de 10 mil registros, divide em dois saveAll para não
        // sobrecarregar o Persistence Context e manter em um tamanho gerenciável,
        // evitando problemas
        if (!eventosList.isEmpty()) {
            int size = eventosList.size();
            int limite = 10_000;

            if (size > limite) {
                List<EventoClinico> parte1 = eventosList.subList(0, limite);
                eventoClinicoRepository.saveAll(parte1);

                List<EventoClinico> parte2 = eventosList.subList(limite, size);
                eventoClinicoRepository.saveAll(parte2);
            } else {
                eventoClinicoRepository.saveAll(eventosList);
            }
        } else {
            logger.info("Nada para ser salvo");
        }
    }

    @Scheduled(cron = "0 0 3 * * *") // Todos os dias 3 da manha
    @Transactional
    public void deletaEventosAntigos() {
        logger.info("Iniciando JOB para para deletar Eventos Clinicos antigos");
        long cutoff = System.currentTimeMillis() - retentionDays * 24L * 60L * 60L * 1000L;
        eventoClinicoRepository.deleteOlderThan(cutoff);
    }

}
