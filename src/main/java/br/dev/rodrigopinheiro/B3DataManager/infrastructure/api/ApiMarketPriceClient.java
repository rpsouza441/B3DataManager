package br.dev.rodrigopinheiro.B3DataManager.infrastructure.api;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.api.ApiClientException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.api.InvalidTickerListException;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model.MarketPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente da API de MarketPrice.
 */
@Component
@Slf4j
public class ApiMarketPriceClient extends AbstractApiClient<MarketPrice> {

    @Value("${api.market.price.url}")
    private String apiUrl;

    /**
     * Construtor do cliente da API de MarketPrice.
     *
     * @param restTemplate  RestTemplate configurado.
     * @param messageSource Fonte de mensagens para internacionalização.
     */
    public ApiMarketPriceClient(RestTemplate restTemplate, MessageSource messageSource) {
        super(restTemplate, messageSource);
    }

    @Override
    protected String getApiUrl() {
        return this.apiUrl;
    }

    @Override
    protected List<MarketPrice> parseResponse(String response) throws Exception {
        return MarketPrice.fromJson(response);
    }

    /**
     * Faz uma requisição à API para obter os preços de mercado dos tickers fornecidos.
     *
     * @param tickers Lista de tickers a serem consultados.
     * @return Lista de objetos {@link MarketPrice} contendo os preços de mercado.
     * @throws InvalidTickerListException Se a lista de tickers for nula ou vazia.
     * @throws ApiClientException         Se ocorrer um erro durante a comunicação com a API.
     */
    public List<MarketPrice> fetchMarketPrices(List<String> tickers) {
        // Mantém a lógica de logging e comentários originais
        return fetchData(tickers);
    }
}
