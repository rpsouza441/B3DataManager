package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import java.math.BigDecimal;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoRendaFixaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoRendaVariavelEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RendaFactory {

    private final ProdutoParser produtoParser;
    private final TipoAtivoFixaMapper tipoAtivoFixaMapper;
    private final TipoAtivoVariavelService tipoAtivoVariavelService;
    private final AtivoFinanceiroRepository ativoFinanceiroRepository;

    public RendaFactory(ProdutoParser produtoParser,
                        TipoAtivoFixaMapper tipoAtivoFixaMapper,
                        TipoAtivoVariavelService tipoAtivoVariavelService, 
                        AtivoFinanceiroRepository ativoFinanceiroRepository) {
        this.produtoParser = produtoParser;
        this.tipoAtivoFixaMapper = tipoAtivoFixaMapper;
        this.tipoAtivoVariavelService = tipoAtivoVariavelService;
        this.ativoFinanceiroRepository = ativoFinanceiroRepository;
    }

    /**
     * Cria uma instância de AtivoRendaFixa com base na operação.
     *
     * @param operacao A operação importada, que contém informações do produto.
     * @return A instância de AtivoRendaFixa devidamente configurada.
     */
    public AtivoRendaFixaEntity criarRendaFixa(OperacaoEntity operacao) {
        String produto = operacao.getProduto();
        
        // Criação e configuração da RendaFixa
        AtivoRendaFixaEntity rendaFixa = new AtivoRendaFixaEntity();
        // Mapeia o tipo de ativo fixo a partir do produto
        TipoAtivoFinanceiroFixa tipoFixa = tipoAtivoFixaMapper.mapear(produto);
        log.debug("Tipo de renda fixa: {}", tipoFixa);
        rendaFixa.setTipoRendaFixa(tipoFixa);
        rendaFixa.setCodigo(produto);
        rendaFixa.setNome(produto);
        return ativoFinanceiroRepository.save(rendaFixa);
    }

    /**
     * Cria uma instância de AtivoRendaVariavel com base na operação.
     *
     * @param operacao A operação importada, que contém informações do produto.
     * @return A instância de AtivoRendaVariavel devidamente configurada.
     */
    public AtivoRendaVariavelEntity criarRendaVariavel(OperacaoEntity operacao) {
        String produto = operacao.getProduto();
        
        // Criação e configuração da RendaVariavel
        AtivoRendaVariavelEntity rendaVariavel = new AtivoRendaVariavelEntity();
        // Para renda variável, o ticker deve ser extraído corretamente
        String ticker = produtoParser.extrairTicker(produto);
        log.debug("Ticker extraido: {}", ticker);
        TipoAtivoFinanceiroVariavel tipoVariavel = tipoAtivoVariavelService.definirTipoAtivo(ticker);
        rendaVariavel.setTipoRendaVariavel(tipoVariavel);
        rendaVariavel.setCodigo(ticker);
        rendaVariavel.setNome(produto);
        return ativoFinanceiroRepository.save(rendaVariavel);
    }
}
