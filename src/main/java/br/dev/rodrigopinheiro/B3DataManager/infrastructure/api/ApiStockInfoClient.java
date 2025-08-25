package br.dev.rodrigopinheiro.B3DataManager.infrastructure.api;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.api.ApiClientException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.api.InvalidTickerListException;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model.StockInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente da API de StockInfo.
 * A API StockInfo fornece todas as informações de um ticker.
 */
@Component
@Slf4j
public class ApiStockInfoClient extends AbstractApiClient<StockInfo> {

    @Value("${api.stock.info.url}")
    private String apiUrl;

    /**
     * Construtor do cliente da API de StockInfo.
     *
     * @param restTemplate  RestTemplate configurado.
     * @param messageSource Fonte de mensagens para internacionalização.
     */
    public ApiStockInfoClient(RestTemplate restTemplate, MessageSource messageSource) {
        super(restTemplate, messageSource);
    }

    @Override
    protected String getApiUrl() {
        return this.apiUrl;
    }

    @Override
    protected List<StockInfo> parseResponse(String response) throws Exception {
        return StockInfo.fromJson(response);
    }

    /**
     * Faz uma requisição à API para obter informações detalhadas sobre os tickers fornecidos.
     *
     * @param tickers Lista de tickers a serem consultados.
     * @return Lista de objetos {@link StockInfo} contendo as informações detalhadas dos ativos.
     * @throws InvalidTickerListException Se a lista de tickers for nula ou vazia.
     * @throws ApiClientException         Se ocorrer um erro durante a comunicação com a API.
     */
    public List<StockInfo> fetchStockInfos(List<String> tickers) {
        // Mantém a lógica de logging e comentários originais
        return fetchData(tickers);
    }
}
