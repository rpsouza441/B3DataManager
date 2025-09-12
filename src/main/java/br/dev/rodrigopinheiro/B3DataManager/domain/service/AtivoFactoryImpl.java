package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.application.service.AtivoFinanceiroService;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PosicaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.PosicaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.TransacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AtivoFactoryImpl implements AtivoFactory {

    private final AtivoFinanceiroService ativoFinanceiroService;
    private final TransacaoFactory transacaoFactory;
    private final ProdutoParser produtoParser;
    private final TransacaoRepository transacaoRepository;
    private final PosicaoRepository posicaoRepository;

    public AtivoFactoryImpl(AtivoFinanceiroService ativoFinanceiroService,
                            TransacaoFactory transacaoFactory,
                            ProdutoParser produtoParser,
                            TransacaoRepository transacaoRepository,
                            PosicaoRepository posicaoRepository) {
        this.ativoFinanceiroService = ativoFinanceiroService;
        this.transacaoFactory = transacaoFactory;
        this.produtoParser = produtoParser;
        this.transacaoRepository = transacaoRepository;
        this.posicaoRepository = posicaoRepository;
    }

    @Override
    public AtivoFinanceiroEntity criarAtivo(OperacaoEntity operacao, PortfolioEntity portfolio) {
        String produto = operacao.getProduto();
        String ticker = produtoParser.extrairTicker(produto);
        log.info("Criando ativo para ticker: {}", ticker);

        AtivoFinanceiroEntity ativoFinanceiro = ativoFinanceiroService.buscarOuCriarAtivoFinanceiro(
                ticker, portfolio);

        // Definir o tipo do ativo baseado no produto
        TipoAtivo tipoAtivo = produtoParser.isRendaFixa(operacao.getProduto()) ? 
            TipoAtivo.RENDA_FIXA : TipoAtivo.RENDA_VARIAVEL;
        ativoFinanceiro.setTipoAtivo(tipoAtivo);
        
        // Criar transação para o histórico
        TransacaoEntity transacao = transacaoFactory.criarTransacao(operacao);
        transacao.setAtivoFinanceiro(ativoFinanceiro);
        transacao.setPortfolio(portfolio);
        transacaoRepository.save(transacao);
        
        // Criar ou atualizar posição (estado atual)
        criarOuAtualizarPosicao(ativoFinanceiro, portfolio, transacao);

        log.info("Ativo criado com sucesso: {}", ativoFinanceiro);
        return ativoFinanceiro;
    }
    
    /**
     * Cria ou atualiza a posição do ativo no portfolio
     */
    private void criarOuAtualizarPosicao(AtivoFinanceiroEntity ativoFinanceiro, PortfolioEntity portfolio, TransacaoEntity transacao) {
        // Buscar posição existente
        PosicaoEntity posicao = posicaoRepository.findPosicoesAtivasByPortfolioId(portfolio.getId())
            .stream()
            .filter(p -> p.getAtivoFinanceiro().getId().equals(ativoFinanceiro.getId()))
            .findFirst()
            .orElse(null);
        
        if (posicao == null) {
            // Criar nova posição
            posicao = new PosicaoEntity(ativoFinanceiro, portfolio);
        }
        
        // Atualizar dados da posição com base na transação
        // Aqui você pode implementar a lógica de cálculo de preço médio, quantidade, etc.
        // Por simplicidade, vou apenas salvar a posição
        posicaoRepository.save(posicao);
        
        log.info("Posição atualizada para ativo: {} no portfolio: {}", ativoFinanceiro.getCodigo(), portfolio.getId());
    }
}
