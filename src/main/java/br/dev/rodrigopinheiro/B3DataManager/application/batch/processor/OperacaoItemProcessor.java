package br.dev.rodrigopinheiro.B3DataManager.application.batch.processor;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.DominioService;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class OperacaoItemProcessor implements ItemProcessor<Operacao, AtivoFinanceiroEntity> {

    private final DominioService dominioService;

    public OperacaoItemProcessor(DominioService dominioService) {
        this.dominioService = dominioService;
    }

    @Override
    public AtivoFinanceiroEntity process(Operacao operacao) {
        return dominioService.criarAtivo(operacao);
    }
}
