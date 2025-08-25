package br.dev.rodrigopinheiro.B3DataManager.domain.service;

/**
 * Interface que define os métodos para extrair informações do produto.
 *
 * Este parser centraliza a extração do ticker, utilizando a seguinte lógica:
 * - Se o produto for de renda fixa, o ticker é obtido pela concatenação dos dois primeiros tokens.
 * - Caso contrário (renda variável), o ticker é obtido utilizando apenas o primeiro token.
 *
 * Os demais métodos auxiliares para extração específica de ativos de renda fixa ou variável
 * não são necessários, pois toda a lógica fica centralizada em {@link #extrairTicker(String)}.
 */
public interface ProdutoParser {

    /**
     * Extrai o ticker do produto com base na lógica legada.
     * Para renda fixa, retorna a concatenação dos dois primeiros tokens.
     * Para renda variável, retorna apenas o primeiro token.
     *
     * Exemplos:
     * - "SAPR11 - Empresa XYZ" retorna "SAPR11"
     * - "CDB - CDBC247FRL8 - MERCADO CREDITO ..." retorna "CDB - CDBC247FRL8"
     *
     * @param produto a string que contém os dados do produto.
     * @return o ticker extraído ou uma string vazia se não for possível extrair.
     */
    String extrairTicker(String produto);

    /**
     * Verifica se o produto representa um ativo de renda fixa.
     *
     * @param produto a string que contém os dados do produto.
     * @return true se for renda fixa; false caso contrário.
     */
    boolean isRendaFixa(String produto);

    /**
     * Extrai o código do ativo para renda fixa.
     * Para a maioria dos ativos de renda fixa (como CDB ou Debênture),
     * espera-se que o código seja o segundo token. Para Tesouro Direto,
     * o formato pode ser diferente, então retorna o produto completo.
     *
     * Exemplos:
     * - "CDB - CDBC247FRL8 - MERCADO CREDITO SOCIEDADE ..." retorna "CDBC247FRL8"
     * - "Tesouro Selic 2024" retorna "Tesouro Selic 2024"
     *
     * @param produto a descrição do ativo.
     * @return o código do ativo para renda fixa.
     */
    String extrairCodigoRendaFixa(String produto);

    /**
     * Retorna o nome completo do ativo conforme informado, sem processamento adicional.
     * Útil para exibição, quando se deseja o texto completo informado.
     *
     * @param produto a string que contém os dados do produto.
     * @return o nome completo do ativo.
     */
    String extrairNomeCompleto(String produto);
}
