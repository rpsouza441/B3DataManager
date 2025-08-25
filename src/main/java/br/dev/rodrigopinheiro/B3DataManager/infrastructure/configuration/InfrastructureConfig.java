package br.dev.rodrigopinheiro.B3DataManager.infrastructure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Classe de configuração para infraestrutura, responsável por criar e configurar beans essenciais para a aplicação.
 */
@Configuration
@Slf4j
public class InfrastructureConfig {

    @Value("${api.preco.token}")
    private String apiToken;

    /**
     * Cria e configura um bean de {@link RestTemplate}, adicionando um interceptor para incluir o token de autorização
     * em todas as requisições HTTP.
     *
     * @return um {@link RestTemplate} configurado com o token de autorização.
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(authInterceptor()));
        log.info("RestTemplate configurado com o token de autorização.");
        return restTemplate;
    }

    /**
     * Cria um interceptor HTTP que adiciona o token de autorização no cabeçalho de cada requisição.
     *
     * @return um {@link ClientHttpRequestInterceptor} configurado para adicionar o token.
     */
    private ClientHttpRequestInterceptor authInterceptor() {
        return (request, body, execution) -> {
            // Adiciona o token de autorização no cabeçalho
            request.getHeaders().add("Authorization", apiToken);
            log.debug("Adicionando Authorization Token: {}", apiToken);
            log.debug("Request URI: {}", request.getURI());
            log.debug("Request Headers: {}", request.getHeaders());
            return execution.execute(request, body);
        };
    }
}
