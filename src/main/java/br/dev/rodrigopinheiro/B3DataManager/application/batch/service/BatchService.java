package br.dev.rodrigopinheiro.B3DataManager.application.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class BatchService {

    private final JobLauncher jobLauncher;
    private final Job processOperacoesJob;

    public BatchService(JobLauncher jobLauncher, Job processOperacoesJob) {
        this.jobLauncher = jobLauncher;
        this.processOperacoesJob = processOperacoesJob;
    }

    public void executeBatch() {
        try {
            log.info("Iniciando o Job: processOperacoesJob");

            // Criação dos parâmetros do Job
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .toJobParameters();

            // Executa o Job
            JobExecution jobExecution = jobLauncher.run(processOperacoesJob, jobParameters);

            // Log do status do Job
            log.info("Job executado com sucesso!");
            log.info("Status do Job: {}", jobExecution.getStatus());
            log.info("Início do Job: {}", jobExecution.getStartTime());
            log.info("Término do Job: {}", jobExecution.getEndTime());

        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("O Job já está em execução: {}", e.getMessage());
        } catch (JobRestartException e) {
            log.error("Erro ao reiniciar o Job: {}", e.getMessage());
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info("O Job já foi completado anteriormente: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao executar o Job: {}", e.getMessage(), e);
        }
    }
}
