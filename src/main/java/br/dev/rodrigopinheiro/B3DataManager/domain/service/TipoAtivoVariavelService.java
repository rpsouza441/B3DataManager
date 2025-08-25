package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.ApiClassifyAssetClient;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model.AssetClassification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface para o serviço responsável por definir o tipo de ativo financeiro variável
 * com base no ticker.
 *
 * Seguindo os princípios de SOLID e DDD, essa interface abstrai a lógica de definição
 * do tipo de ativo, permitindo desacoplamento e facilitando testes.
 */
public interface TipoAtivoVariavelService {


    /**
     * Define o tipo de ativo financeiro variável com base no ticker informado.
     *
     * @param ticker O ticker do ativo (ex.: "sapr11", "sapr3").
     * @return Um valor do enum TipoAtivoFinanceiroVariavel correspondente.
     */
    TipoAtivoFinanceiroVariavel definirTipoAtivo(String ticker);
}
