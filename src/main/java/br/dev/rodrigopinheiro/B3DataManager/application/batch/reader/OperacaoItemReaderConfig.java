package br.dev.rodrigopinheiro.B3DataManager.application.batch.reader;

import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.GetOperacoesForBatchUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.batch.CustomOperacaoItemReader;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do ItemReader para processamento batch de operações.
 * 
 * <p>Migrado para usar arquitetura hexagonal com Use Cases ao invés de
 * acessar diretamente repositórios JPA.</p>
 */
@Configuration
public class OperacaoItemReaderConfig {

    private static final int DEFAULT_PAGE_SIZE = 100;
    
    /**
     * Cria ItemReader customizado usando arquitetura hexagonal.
     * 
     * @param getOperacoesForBatchUseCase Use Case para buscar operações
     * @return ItemReader configurado para operações não dimensionadas e não duplicadas
     */
    @Bean
    public ItemReader<Operacao> operacaoItemReader(GetOperacoesForBatchUseCase getOperacoesForBatchUseCase) {
        return new CustomOperacaoItemReader(
            getOperacoesForBatchUseCase,
            DEFAULT_PAGE_SIZE,
            false, // dimensionado = false
            false  // duplicado = false
        );
    }
}
