package br.dev.rodrigopinheiro.B3DataManager.application.batch.config;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuração principal do Spring Batch para processamento de operações financeiras.
 * 
 * Centraliza a configuração de jobs e steps para processamento em lote, incluindo:
 * - Job de processamento de operações
 * - Step de transformação de dados
 * - Configuração de chunk processing
 * - Integração com transações
 * 
 * Características:
 * - Processamento em chunks de 10 itens
 * - Transações gerenciadas automaticamente
 * - Reader, Processor e Writer configuráveis
 * - Integração com JobRepository
 * - Suporte a restart e recovery
 * - Logging detalhado de execução
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job processOperacoesJob(Step processStep) {
        return new JobBuilder("processOperacoesJob", jobRepository)
                .start(processStep)
                .build();
    }

    
    @Bean
    public Step processStep(
            ItemReader<Operacao> reader,
            ItemProcessor<Operacao, AtivoFinanceiroEntity> processor,
            ItemWriter<AtivoFinanceiroEntity> ativoItemWriter
    ) {
        return new StepBuilder("processStep", jobRepository)
                .<Operacao, AtivoFinanceiroEntity>chunk(10, transactionManager) // Processa 10 itens por vez
                .reader(reader)
                .processor(processor)
                .writer(ativoItemWriter)
                .build();
    }
}
