package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import java.math.BigDecimal;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaFixaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaVariavelEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.RendaFixaRepository;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.RendaVariavelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RendaFactory {

    private final ProdutoParser produtoParser;
    private final TipoAtivoFixaMapper tipoAtivoFixaMapper;
    private final TipoAtivoVariavelService tipoAtivoVariavelService;
    private final RendaFixaRepository rendaFixaRepository;
    private final RendaVariavelRepository rendaVariavelRepository;

    public RendaFactory(ProdutoParser produtoParser,
                        TipoAtivoFixaMapper tipoAtivoFixaMapper,
                        TipoAtivoVariavelService tipoAtivoVariavelService, RendaFixaRepository rendaFixaRepository, RendaVariavelRepository rendaVariavelRepository) {
        this.produtoParser = produtoParser;
        this.tipoAtivoFixaMapper = tipoAtivoFixaMapper;
        this.tipoAtivoVariavelService = tipoAtivoVariavelService;
        this.rendaFixaRepository = rendaFixaRepository;
        this.rendaVariavelRepository = rendaVariavelRepository;
    }

    /**
     * Cria uma instância de RendaFixa com base na operação.
     *
     * @param operacao A operação importada, que contém informações do produto.
     * @return A instância de RendaFixa devidamente configurada.
     */
    public RendaFixaEntity criarRendaFixa(OperacaoEntity operacao) {
        String produto = operacao.getProduto();
        
        // Criação e configuração da RendaFixa
        RendaFixaEntity rendaFixa = new RendaFixaEntity();
        // Mapeia o tipo de ativo fixo a partir do produto
        TipoAtivoFinanceiroFixa tipoFixa = tipoAtivoFixaMapper.mapear(produto);
        log.debug("Tipo de renda fixa: {}", tipoFixa);
        rendaFixa.setTipoRendaFixa(tipoFixa);
        // Para renda fixa, o preço unitário, data, quantidade e total vêm da transação
        rendaFixa.setPrecoUnitario(operacao.getPrecoUnitario());
        rendaFixa.setDataCompra(operacao.getData());
        rendaFixa.setQuantidade(operacao.getQuantidade());
        rendaFixa.setTotal(operacao.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(operacao.getQuantidade())));
        return rendaFixaRepository.save(rendaFixa);
    }

    /**
     * Cria uma instância de RendaVariavel com base na operação.
     *
     * @param operacao A operação importada, que contém informações do produto.
     * @return A instância de RendaVariavel devidamente configurada.
     */
    public RendaVariavelEntity criarRendaVariavel(OperacaoEntity operacao) {
        String produto = operacao.getProduto();
        
        // Criação e configuração da RendaVariavel
        RendaVariavelEntity rendaVariavel = new RendaVariavelEntity();
        // Para renda variável, o ticker deve ser extraído corretamente
        String ticker = produtoParser.extrairTicker(produto);
        log.debug("Ticker extraido: {}", ticker);
        TipoAtivoFinanceiroVariavel tipoVariavel = tipoAtivoVariavelService.definirTipoAtivo(ticker);
        rendaVariavel.setTipoRendaVariavel(tipoVariavel);
        rendaVariavel.setPrecoUnitario(operacao.getPrecoUnitario());
        rendaVariavel.setDataCompra(operacao.getData());
        rendaVariavel.setQuantidade(operacao.getQuantidade());
        rendaVariavel.setTotal(operacao.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(operacao.getQuantidade())));
        return rendaVariavelRepository.save(rendaVariavel);
    }
}
