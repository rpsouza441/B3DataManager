package br.dev.rodrigopinheiro.B3DataManager.infrastructure.api;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.api.ApiClientException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.api.InvalidTickerListException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractApiClient<T> {

    protected final RestTemplate restTemplate;
    protected final MessageSource messageSource;

    protected AbstractApiClient(RestTemplate restTemplate, MessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.messageSource = messageSource;
    }

    /**
     * Retorna a URL da API a ser utilizada.
     */
    protected abstract String getApiUrl();

    /**
     * Converte a resposta JSON para uma lista de objetos T.
     *
     * @param response A resposta JSON da API.
     * @return Lista de objetos T.
     * @throws Exception Caso ocorra erro na conversão.
     */
    protected abstract List<T> parseResponse(String response) throws Exception;

    /**
     * Método template que executa a chamada à API.
     *
     * @param tickers Lista de tickers a serem consultados.
     * @return Lista de objetos T contendo os dados da API.
     * @throws InvalidTickerListException Se a lista de tickers for nula ou vazia.
     * @throws ApiClientException         Se ocorrer um erro durante a comunicação com a API.
     */
    protected List<T> fetchData(List<String> tickers) {
        log.info("Iniciando chamada à API para os tickers: {}", tickers);

        // Valida a entrada: lista de tickers não pode ser nula ou vazia
        if (tickers == null || tickers.isEmpty()) {
            log.warn("A lista de tickers fornecida está vazia ou nula.");
            throw new InvalidTickerListException(messageSource);
        }

        try {
            // Prepara o corpo da requisição
            Map<String, List<String>> requestBody = Map.of("tickers", tickers);

            // Configura os cabeçalhos HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, List<String>>> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("Enviando requisição para a API em {}", getApiUrl());
            String response = restTemplate.postForObject(getApiUrl(), requestEntity, String.class);

            log.debug("Resposta recebida da API: {}", response);

            // Converte a resposta JSON para uma lista de objetos T
            List<T> data = parseResponse(response);

            log.info("Processamento concluído. Total de itens processados: {}", data.size());

            return data;
        } catch (Exception e) {
            log.error("Erro ao acessar a API: {}", e.getMessage(), e);
            throw new ApiClientException(messageSource, e.getMessage());
        }
    }
}
