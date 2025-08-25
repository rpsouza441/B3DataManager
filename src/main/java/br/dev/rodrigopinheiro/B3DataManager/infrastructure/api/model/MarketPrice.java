package br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Representa o preço de mercado de um ativo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) //  Isso garante que campos desconhecidos sejam ignorados
public class MarketPrice {
    private String ticker; // Identificador do ativo
    private BigDecimal price;  // Preço de mercado atual

    /**
     * Converte um JSON representando uma lista de MarketPrice em uma lista de objetos MarketPrice.
     *
     * @param jsonString JSON representando a lista de MarketPrice.
     * @return Lista de objetos MarketPrice.
     * @throws Exception Caso ocorra erro na conversão.
     */
    public static List<MarketPrice> fromJson(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Converte o JSON em uma lista de MarketPrice
        return objectMapper.readValue(jsonString, new TypeReference<List<MarketPrice>>() {
        });
    }


}
