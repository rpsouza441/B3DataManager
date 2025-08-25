package br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Representa informações detalhadas sobre uma ação ou ativo financeiro.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInfo {
    // Identificação
    private String uuid; // Identificador único do ativo no Yahoo Finance.
    private String currency; // Moeda utilizada nos preços e valores do ativo.
    private String longName; // Nome completo da ação ou ativo financeiro.
    private String shortName; // Nome curto utilizado no mercado para identificação.
    private String quoteType; // Tipo de ativo (ex.: "EQUITY" para ações).

    // Identificação de mercado
    private String symbol; // Símbolo do ativo na bolsa (ex.: "BBAS3.SA").
    private String exchange; // Nome da bolsa onde o ativo é negociado.
    private long firstTradeDateEpochUtc; // Data do primeiro dia de negociação (em timestamp Unix).

    // Dados financeiros
    private BigDecimal marketCap; // Capitalização de mercado (valor total das ações em circulação).
    private BigDecimal previousClose; // Preço de fechamento do último pregão.
    private BigDecimal currentPrice; // Preço atual da ação.
    private BigDecimal bid; // Preço máximo de compra ofertado atualmente.
    private BigDecimal ask; // Preço mínimo de venda ofertado atualmente.

    // Desempenho
    private BigDecimal week52Change; // Variação percentual do preço em 52 semanas.
    private BigDecimal sandP52WeekChange; // Variação percentual do S&P 500 no mesmo período.
    private BigDecimal beta; // Beta da ação (volatilidade em relação ao mercado).

    // Localização
    private String city; // Cidade da sede da empresa.
    private String country; // País da sede da empresa.

    // Dados de volume
    private long averageVolume; // Volume médio de negociação em determinado período.

    // Riscos
    private int boardRisk; // Risco relacionado ao conselho administrativo (escala de 1 a 10).
    private int compensationRisk; // Risco relacionado à compensação executiva (escala de 1 a 10).

    // Contabilidade e liquidez
    private BigDecimal bookValue; // Valor contábil por ação.
    private BigDecimal currentRatio; // Índice de liquidez corrente (ativos circulantes / passivos circulantes).

    // Dividendos
    private BigDecimal dividendRate; // Valor anual dos dividendos pagos por ação.
    private BigDecimal dividendYield; // Rentabilidade percentual dos dividendos em relação ao preço da ação.
    private BigDecimal lastDividendDate; // Data do último pagamento de dividendos (em timestamp Unix).
    private BigDecimal lastDividendValue; // Valor do último dividendo pago por ação.

    // Crescimento e margens
    private BigDecimal earningsGrowth; // Crescimento percentual do lucro.
    private BigDecimal profitMargins; // Margem de lucro líquido em relação à receita.
    private BigDecimal grossMargins; // Margem bruta em relação à receita total.
    private BigDecimal revenueGrowth; // Crescimento percentual da receita.

    // Desempenho operacional
    private BigDecimal enterpriseValue; // Valor total da empresa, incluindo dívidas e caixa.
    private BigDecimal freeCashflow; // Fluxo de caixa livre (após despesas operacionais e de capital).
    private BigDecimal operatingCashflow; // Fluxo de caixa gerado pelas operações principais.
    private BigDecimal trailingEps; // Lucro por ação nos últimos 12 meses.
    private BigDecimal trailingAnnualDividendRate; // Taxa anual de dividendos paga nos últimos 12 meses.
    private BigDecimal trailingAnnualDividendYield; // Rentabilidade percentual dos dividendos nos últimos 12 meses.

    // Receita e dívida
    private BigDecimal totalRevenue; // Receita total da empresa.
    private BigDecimal revenuePerShare; // Receita por ação.
    private BigDecimal totalCash; // Caixa total disponível na empresa.
    private BigDecimal totalCashPerShare; // Caixa disponível por ação.
    private BigDecimal totalDebt; // Total de dívidas da empresa.
    private BigDecimal debtToEquity; // Relação entre dívida e patrimônio líquido.

    // Preços-alvo e métricas futuras
    private long sharesOutstanding; // Número total de ações em circulação.
    private BigDecimal targetHighPrice; // Preço-alvo mais alto estimado por analistas.
    private BigDecimal targetLowPrice; // Preço-alvo mais baixo estimado por analistas.
    private BigDecimal targetMeanPrice; // Preço-alvo médio estimado por analistas.
    private BigDecimal targetMedianPrice; // Preço-alvo mediano estimado por analistas.
    private BigDecimal forwardPE; // Relação preço/lucro esperada para os próximos 12 meses.

    // Retornos
    private BigDecimal returnOnAssets; // Retorno sobre ativos (ROA).
    private BigDecimal returnOnEquity; // Retorno sobre patrimônio líquido (ROE).

    // Informações dinâmicas
    private String industrySector; // Setor ou indústria do ativo (preenchido dinamicamente).

    // Informações de divisões
    private BigDecimal impliedSharesOutstanding; // Número implícito de ações baseado no preço.
    private long lastSplitDate; // Data do último desdobramento de ações (em timestamp Unix).
    private String lastSplitFactor; // Fator do desdobramento (ex.: "2:1").

    // Recomendações e análises
    private String recommendationKey; // Recomendações dos analistas (ex.: "buy", "hold").
    private BigDecimal recommendationMean; // Média das recomendações dos analistas.

    // Lucros e margens operacionais
    private BigDecimal grossProfits; // Lucro bruto (receita - custo dos produtos vendidos).
    private BigDecimal operatingMargins; // Margens operacionais (lucro operacional / receita total).

    /**
     * Converte um JSON em uma instância de StockInfo.
     *
     * @param jsonString JSON representando os dados de uma ação.
     * @return Instância de StockInfo preenchida.
     * @throws Exception Caso ocorra erro na conversão.
     */
    public static List<StockInfo> fromJson(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Lê o JSON como lista de StockInfo
        List<StockInfo> stockInfos = objectMapper.readValue(jsonString, new TypeReference<List<StockInfo>>() {});

        // Para cada objeto, preenche o campo industrySector dinamicamente
        for (StockInfo stockInfo : stockInfos) {
            JsonNode rootNode = objectMapper.valueToTree(stockInfo);
            stockInfo.setIndustrySector(extractIndustrySector(rootNode));
        }

        return stockInfos;
    }

    /**
     * Extrai o campo industrySector de forma dinâmica.
     *
     * @param rootNode Nó raiz do JSON.
     * @return Valor do campo industrySector formatado.
     */
    private static String extractIndustrySector(JsonNode rootNode) {
        String[] possibleFields = {"sector", "industry", "sectorDisp", "industryDisp", "sectorKey", "industryKey"};

        for (String field : possibleFields) {
            if (rootNode.has(field)) {
                String value = rootNode.get(field).asText(null);
                if (value != null && !value.isEmpty()) {
                    return value.replace("-", " ").toUpperCase();
                }
            }
        }

        return "UNKNOWN"; // Valor padrão caso nenhum campo seja encontrado
    }

}
