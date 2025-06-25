package com.tcc.epidemiologia.service;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DroolsService {

    private final KieSession kieSession;
    private static final Logger logger = LoggerFactory.getLogger(DroolsService.class);

    public DroolsService(KieSession kieSession) {
        this.kieSession = kieSession;

        // Drools Active Mode -> Ativa a(s) regra(s) automaticamente assim que uma condição é satisfeita
        new Thread(this.kieSession::fireAllRules).start();
    }

    public FactHandle insert(Object object) {
        logger.info("Inserting {} into Drools", object.getClass());
        return kieSession.insert(object);
    }

}
