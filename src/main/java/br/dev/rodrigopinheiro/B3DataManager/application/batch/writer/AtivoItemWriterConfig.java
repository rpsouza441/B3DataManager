package br.dev.rodrigopinheiro.B3DataManager.application.batch.writer;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtivoItemWriterConfig {

    @Bean
    public RepositoryItemWriter<AtivoFinanceiro> ativoItemWriter(AtivoFinanceiroRepository ativoFinanceiroRepository) {
        RepositoryItemWriter<AtivoFinanceiro> writer = new RepositoryItemWriter<>();
        writer.setRepository(ativoFinanceiroRepository);
        writer.setMethodName("save");
        return writer;
    }
}
