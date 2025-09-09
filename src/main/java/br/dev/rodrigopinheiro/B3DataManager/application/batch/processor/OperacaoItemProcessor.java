package br.dev.rodrigopinheiro.B3DataManager.application.batch.processor;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor para conversão de Operacao em AtivoFinanceiroEntity
 * TODO: Implementar lógica de conversão ou usar UseCase apropriado
 */
@Component
public class OperacaoItemProcessor implements ItemProcessor<Operacao, AtivoFinanceiroEntity> {

    public OperacaoItemProcessor() {
        // Construtor sem dependências por enquanto
    }

    @Override
    public AtivoFinanceiroEntity process(Operacao operacao) {
        // TODO: Implementar lógica de conversão adequada
        // Por enquanto retorna uma entidade vazia para não quebrar o build
        AtivoFinanceiroEntity ativo = new AtivoFinanceiroEntity();
        // Implementar conversão de Operacao para AtivoFinanceiroEntity
        return ativo;
    }
}
