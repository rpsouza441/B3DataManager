package br.dev.rodrigopinheiro.B3DataManager.application.batch.processor;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.DominioService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class OperacaoItemProcessor implements ItemProcessor<Operacao, AtivoFinanceiro> {

    private final DominioService dominioService;

    public OperacaoItemProcessor(DominioService dominioService) {
        this.dominioService = dominioService;
    }

    @Override
    public AtivoFinanceiro process(Operacao operacao) {
        return dominioService.criarAtivo(operacao);
    }
}
