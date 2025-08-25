package br.dev.rodrigopinheiro.B3DataManager.infrastructure.api;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model.AssetClassification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente da API de Classificação de Ativos.
 * Essa API retorna se um ticker, especialmente os que finalizam com "11", é Unit, ETF ou FII.
 */
@Component
@Slf4j
public class ApiClassifyAssetClient extends AbstractApiClient<AssetClassification> {

    @Value("${api.classify.asset.url}")
    private String apiUrl;

    /**
     * Construtor do cliente da API de Classificação de Ativos.
     *
     * @param restTemplate  RestTemplate configurado.
     * @param messageSource Fonte de mensagens para internacionalização.
     */
    public ApiClassifyAssetClient(RestTemplate restTemplate, MessageSource messageSource) {
        super(restTemplate, messageSource);
    }

    @Override
    protected String getApiUrl() {
        return this.apiUrl;
    }

    @Override
    protected List<AssetClassification> parseResponse(String response) throws Exception {
        return AssetClassification.fromJson(response);
    }

    /**
     * Faz uma requisição à API para obter a classificação dos tickers fornecidos.
     *
     * @param tickers Lista de tickers a serem consultados.
     * @return ListaInvalidTickerListException de objetos {@link AssetClassification} contendo a classificação dos ativos.
     * @throws  Se a lista de tickers for nula ou vazia.
     * @throws ApiClientException         Se ocorrer um erro durante a comunicação com a API.
     */
    public List<AssetClassification> fetchAssetClassifications(List<String> tickers) {
        // Mantém a lógica de logging e comentários originais
        return fetchData(tickers);
    }
}
