package com.kibocommerce.bpm.fulfillment.config;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.*;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

@Configuration
public class JBPMTransactionConfig {

    @Inject
    private EntityManagerFactory entityManagerFactory;

    @Inject
    private PlatformTransactionManager transactionManager;

    @Bean
    public RuntimeManager runtimeManager() {
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
            .newDefaultBuilder()
            .entityManagerFactory(entityManagerFactory)
            .persistence(true)
            .transactionManager((TransactionManager) ((JtaTransactionManager) transactionManager).getTransactionManager())
            .addEnvironmentEntry("org.kie.api.runtime.EnvironmentName.TRANSACTION_MANAGER", transactionManager);

        return RuntimeManagerFactory.Factory.get()
            .newPerRequestRuntimeManager(builder.get(), "com.kibocommerce.bpm:fulfillment:1.0.0");
    }

    @Bean
    public KieSession kieSession(RuntimeManager runtimeManager) {
        return runtimeManager.getRuntimeEngine(EmptyContext.get()).getKieSession();
    }

    @Bean
    public KieContainer kieContainer() {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        return ks.newKieContainer(kr.getDefaultReleaseId());
    }

    @Bean
    public KieBase kieBase(KieContainer kContainer) {
        return kContainer.getKieBase();
    }
}
