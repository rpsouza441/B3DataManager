package br.dev.rodrigopinheiro.B3DataManager.application.batch.config;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
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
            ItemProcessor<Operacao, AtivoFinanceiro> processor,
            ItemWriter<AtivoFinanceiro> writer
    ) {
        return new StepBuilder("processStep", jobRepository)
                .<Operacao, AtivoFinanceiro>chunk(10, transactionManager) // Processa 10 itens por vez
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
