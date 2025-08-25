package br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

/**
 * Representa a classificação de um ativo conforme retornado pela API.
 * Contém os dados retornados e possibilita o mapeamento para o enum TipoAtivoFinanceiroVariavel.
 */
@Getter
@Setter
@ToString
public class AssetClassification {

    private String category;
    private String longBusinessSummary;
    private String longName;
    private String shortName;
    private String ticker;

    /**
     * Converte uma resposta JSON para uma lista de objetos AssetClassification.
     *
     * Exemplo de JSON recebido:
     * [
     *     {
     *         "category": "FII",
     *         "longBusinessSummary": "maxi renda fundo de investimento imobiliario - fii specializes in real estate investments.",
     *         "longName": "maxi renda fundo de investimento imobiliario - fii",
     *         "shortName": "fii maxi renci",
     *         "ticker": "MXRF11.SA"
     *     },
     *     ...
     * ]
     *
     * @param response A resposta JSON contendo as classificações dos ativos.
     * @return Lista de AssetClassification.
     * @throws JsonProcessingException Caso ocorra erro durante a conversão do JSON.
     */
    public static List<AssetClassification> fromJson(String response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, new TypeReference<List<AssetClassification>>() {});
    }

    /**
     * Mapeia a propriedade 'category' retornada pela API para o enum TipoAtivoFinanceiroVariavel.
     * Se a categoria não corresponder a nenhum dos valores esperados, retorna DESCONHECIDO.
     *
     * Por exemplo:
     * - "FII"   -> TipoAtivoFinanceiroVariavel.FII
     * - "ETF"   -> TipoAtivoFinanceiroVariavel.ETF
     * - "UNIT"  -> TipoAtivoFinanceiroVariavel.ACAO_UNIT
     * - Qualquer outro valor (como "Unknown") -> TipoAtivoFinanceiroVariavel.DESCONHECIDO
     *
     * @return O valor do enum TipoAtivoFinanceiroVariavel correspondente.
     */
    public TipoAtivoFinanceiroVariavel toTipoAtivoFinanceiroVariavel() {
        if (category == null) {
            return TipoAtivoFinanceiroVariavel.DESCONHECIDO;
        }
        switch (category.toUpperCase()) {
            case "FII":
                return TipoAtivoFinanceiroVariavel.FII;
            case "ETF":
                return TipoAtivoFinanceiroVariavel.ETF;
            case "UNIT":
                return TipoAtivoFinanceiroVariavel.ACAO_UNIT;
            default:
                return TipoAtivoFinanceiroVariavel.DESCONHECIDO;
        }
    }
}
