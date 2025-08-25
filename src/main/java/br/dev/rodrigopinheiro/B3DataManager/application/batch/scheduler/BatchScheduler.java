package br.dev.rodrigopinheiro.B3DataManager.application.batch.scheduler;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job processOperacoesJob;

    @Value("${batch.scheduler.retry.maxAttempts:3}") // Max retries com valor padrão 3
    private int maxAttempts;

    @Value("${batch.scheduler.retry.waitDuration:5000}") // Tempo de espera entre retries (ms)
    private long waitDuration;

    public BatchScheduler(JobLauncher jobLauncher, Job processOperacoesJob) {
        this.jobLauncher = jobLauncher;
        this.processOperacoesJob = processOperacoesJob;
    }

    /**
     * Agendamento dinâmico utilizando o cron configurado no bean.
     */
    @Retry(name = "batchRetry", fallbackMethod = "handleFailure")
    @Scheduled(cron = "#{@batchSchedulerCron}")
    public void executeBatchJob() {
        try {
            // Parâmetros únicos para garantir execução não duplicada
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .toJobParameters();

            // Executa o Job
            JobExecution jobExecution = jobLauncher.run(processOperacoesJob, jobParameters);

            // Logs estruturados do Job
            log.info("Job executado com sucesso!");
            log.info("Status do Job: {}", jobExecution.getStatus());
            log.info("Início do Job: {}", jobExecution.getStartTime());
            log.info("Término do Job: {}", jobExecution.getEndTime());

        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("O job já está em execução: {}", e.getMessage());
        } catch (JobRestartException e) {
            log.error("Erro ao reiniciar o job: {}", e.getMessage());
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info("O job já foi completado anteriormente: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao executar o job: {}", e.getMessage(), e);
        }
    }

    /**
     * Método de fallback chamado após falha em todas as tentativas de retry.
     */
    public void handleFailure(Throwable throwable) {
        log.error("Falha ao executar o job após {} tentativas. Erro: {}", maxAttempts, throwable.getMessage(), throwable);
    }
}
