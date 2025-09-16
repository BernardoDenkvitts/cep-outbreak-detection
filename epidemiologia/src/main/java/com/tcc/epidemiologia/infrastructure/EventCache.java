package com.tcc.epidemiologia.infrastructure;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Cache Global que será utilizado para evitar 
 * ter vários eventos que representem possível caso de SRAG
 *  vindos de uma mesma pessoa dentro de um mesmo dia
 */
@Service
public class EventCache {

    private static final Logger logger = LogManager.getLogger(EventCache.class);

    private final Cache<Long, Boolean> cache = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS) // TTL de 24h
            .maximumSize(50000)
            .build();

    public void insert(Long id) {
        logger.info("[CACHE] Inserindo evento com ID " + id);
        cache.put(id, Boolean.TRUE);
    }

    // Verifica se o paciente já teve evento de SRAG nas últimas 24h
    public boolean contains(Long id) {
        return cache.getIfPresent(id) != null;
    }

}
