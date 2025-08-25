package br.dev.rodrigopinheiro.B3DataManager.application.batch.reader;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.OperacaoRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class OperacaoItemReaderConfig {

    @Bean
    public RepositoryItemReader<Operacao> operacaoItemReader(OperacaoRepository operacaoRepository) {
        RepositoryItemReader<Operacao> reader = new RepositoryItemReader<>();
        reader.setRepository(operacaoRepository);
        reader.setMethodName("findByDimensionadoAndDuplicado");
        reader.setArguments(Arrays.asList(false, false)); // Dois argumentos: dimensionado=false, duplicado=false
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC)); // Ordena por ID
        return reader;
    }
}
