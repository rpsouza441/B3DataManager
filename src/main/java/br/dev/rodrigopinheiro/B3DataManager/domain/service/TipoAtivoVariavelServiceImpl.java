package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.ApiClassifyAssetClient;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model.AssetClassification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Implementação concreta da interface TipoAtivoVariavelService.
 *
 * Este serviço é responsável por definir o tipo de ativo financeiro variável a partir do ticker,
 * delegando a classificação de ativos com sufixo "11" à API de classificação.
 */
@Service
@Slf4j
public class TipoAtivoVariavelServiceImpl implements TipoAtivoVariavelService {

    private final ApiClassifyAssetClient apiClassifyAssetClient;

    public TipoAtivoVariavelServiceImpl(ApiClassifyAssetClient apiClassifyAssetClient) {
        this.apiClassifyAssetClient = apiClassifyAssetClient;
    }

    /**
     * Define o tipo de ativo financeiro variável com base no ticker informado.
     * Se o ticker possuir sufixo "11", invoca a API de classificação para
     * diferenciar entre FII, ETF ou Unit.
     *
     * @param ticker O ticker do ativo (ex.: "sapr11", "sapr3").
     * @return Um valor do enum TipoAtivoFinanceiroVariavel correspondente.
     */
    public TipoAtivoFinanceiroVariavel definirTipoAtivo(String ticker) {
        log.info("Iniciando definição do tipo para o ticker: {}", ticker);

        if (ticker == null || ticker.isEmpty()) {
            log.error("Ticker nulo ou vazio. Retornando DESCONHECIDO.");
            return TipoAtivoFinanceiroVariavel.DESCONHECIDO;
        }

        String sufixo = extrairSufixo(ticker);

        // Analisa o tipo com base no sufixo
        switch (sufixo) {
            case "3":
                log.info("Ticker identificado como Ação Ordinária (ON).");
                return TipoAtivoFinanceiroVariavel.ACAO_ON;
            case "4":
                log.info("Ticker identificado como Ação Preferencial (PN).");
                return TipoAtivoFinanceiroVariavel.ACAO_PN;
            case "5":
                log.info("Ticker identificado como Ação Preferencial Classe A (PNA).");
                return TipoAtivoFinanceiroVariavel.ACAO_PNA;
            case "6":
                log.info("Ticker identificado como Ação Preferencial Classe B (PNB).");
                return TipoAtivoFinanceiroVariavel.ACAO_PNB;
            case "7":
                log.info("Ticker identificado como Ação Preferencial Classe C (PNC).");
                return TipoAtivoFinanceiroVariavel.ACAO_PNC;
            case "8":
                log.info("Ticker identificado como Ação Preferencial Classe D (PND).");
                return TipoAtivoFinanceiroVariavel.ACAO_PND;
            case "11":
                log.info("Ticker com sufixo 11 detectado. Diferenciando ativo como FII, ETF ou Unit.");
                return diferenciarAtivoComSufixo11(ticker);
            case "1":
                log.info("Ticker identificado como Direito de Subscrição de Ação Ordinária.");
                return TipoAtivoFinanceiroVariavel.DIREITO_SUBSCRICAO_ON;
            case "2":
                log.info("Ticker identificado como Direito de Subscrição de Ação Preferencial.");
                return TipoAtivoFinanceiroVariavel.DIREITO_SUBSCRICAO_PN;
            case "9":
                log.info("Ticker identificado como Recibo de Subscrição de Ação Ordinária.");
                return TipoAtivoFinanceiroVariavel.RECIBO_SUBSCRICAO_ON;
            case "10":
                log.info("Ticker identificado como Recibo de Subscrição de Ação Preferencial.");
                return TipoAtivoFinanceiroVariavel.RECIBO_SUBSCRICAO_PN;
            default:
                log.warn("Sufixo não identificado: {}. Retornando DESCONHECIDO.", sufixo);
                return TipoAtivoFinanceiroVariavel.DESCONHECIDO;
        }
    }

    /**
     * Extrai o sufixo numérico do ticker.
     * Exemplo: "sapr11" ou "SAPR11.SA" resultam em "11".
     *
     * @param ticker O ticker do ativo.
     * @return O sufixo numérico extraído.
     */
    private String extrairSufixo(String ticker) {
        // Remove sufixo de bolsa, se houver (ex.: ".SA")
        String normalized = ticker.toUpperCase().replaceAll("\\.SA$", "");
        StringBuilder suffix = new StringBuilder();
        for (int i = normalized.length() - 1; i >= 0; i--) {
            char c = normalized.charAt(i);
            if (Character.isDigit(c)) {
                suffix.insert(0, c);
            } else {
                break;
            }
        }
        return suffix.toString();
    }

    /**
     * Para tickers com sufixo "11", chama a API de classificação para
     * diferenciar se o ativo é FII, ETF ou Unit.
     *
     * @param ticker O ticker do ativo.
     * @return O tipo de ativo financeiro variável conforme classificação da API.
     */
    private TipoAtivoFinanceiroVariavel diferenciarAtivoComSufixo11(String ticker) {
        try {
            List<AssetClassification> classifications = apiClassifyAssetClient.fetchAssetClassifications(List.of(ticker));
            if (classifications != null && !classifications.isEmpty()) {
                AssetClassification classification = classifications.get(0);
                return classification.toTipoAtivoFinanceiroVariavel();
            }
        } catch (Exception e) {
            log.error("Erro ao diferenciar ativo com sufixo 11 para o ticker {}: {}", ticker, e.getMessage(), e);
        }
        return TipoAtivoFinanceiroVariavel.DESCONHECIDO;
    }
}
