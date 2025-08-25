package br.dev.rodrigopinheiro.B3DataManager.application.batch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchSchedulerConfig {

    /**
     * Bean que fornece a expressão cron para o agendamento do Batch.
     * Configurável via `application.properties` ou variável de sistema.
     */
    @Value("${batch.scheduler.cron:0 0 1 * * ?}") // Padrão: 01:00 AM
    private String cronExpression;

    @Bean(name = "batchSchedulerCron")
    public String batchSchedulerCron() {
        return cronExpression;
    }
}
