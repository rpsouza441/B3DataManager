package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.application.service.AtivoFinanceiroService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AtivoFactoryImpl implements AtivoFactory {

    private final AtivoFinanceiroService ativoFinanceiroService;
    private final RendaFactory rendaFactory;
    private final ProdutoParser produtoParser;

    public AtivoFactoryImpl(AtivoFinanceiroService ativoFinanceiroService,
                            RendaFactory rendaFactory,
                            ProdutoParser produtoParser) {
        this.ativoFinanceiroService = ativoFinanceiroService;
        this.rendaFactory = rendaFactory;
        this.produtoParser = produtoParser;
    }

    @Override
    public AtivoFinanceiro criarAtivo(Operacao operacao, Portfolio portfolio) {
        String produto = operacao.getProduto();
        String ticker = produtoParser.extrairTicker(produto);
        log.info("Criando ativo para ticker: {}", ticker);

        AtivoFinanceiro ativoFinanceiro = ativoFinanceiroService.buscarOuCriarAtivoFinanceiro(
                ticker, portfolio);

        Renda renda = rendaFactory.criarRenda(operacao);
        ativoFinanceiro.adicionarRenda(renda);

        log.info("Ativo criado com sucesso: {}", ativoFinanceiro);
        return ativoFinanceiro;
    }
}
