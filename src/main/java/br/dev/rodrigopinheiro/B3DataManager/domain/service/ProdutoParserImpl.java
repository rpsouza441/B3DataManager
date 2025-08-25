package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementação do ProdutoParser que centraliza a lógica de extração do ticker.
 * <p>
 * - Para ativos de renda fixa (exceto Tesouro), o ticker é extraído pela concatenação dos dois primeiros tokens.
 * - Para Tesouro, é retornado o nome completo do ativo.
 * - Para ativos de renda variável, é extraído apenas o primeiro token, filtrando um padrão de 4 letras seguidas de 1 ou 2 números.
 */
@Slf4j
@Service
public class ProdutoParserImpl implements ProdutoParser {

    // Regex para identificar ativos de renda fixa (CDB, LCI, LCA, TESOURO, DEB)
    private static final Pattern RENDA_FIXA_PATTERN = Pattern.compile("^\\s*(CDB|LCI|LCA|TESOURO|DEB)\\b", Pattern.CASE_INSENSITIVE);

    /**
     * Extrai o ticker do produto.
     * Se o produto for de renda fixa, utiliza a concatenação dos dois primeiros tokens;
     * caso contrário, utiliza o primeiro token.
     */
    @Override
    public String extrairTicker(String produto) {
        if (produto == null || produto.trim().isEmpty()) {
            return "";
        }
        String trimmed = produto.trim();
        // Tratamento especial para Tesouro: retorna o nome completo do ativo.
        if (trimmed.toUpperCase().startsWith("TESOURO")) {
            log.info("Tesouro: {}", produto);
            return trimmed;
        }
        // Para os demais ativos de renda fixa
        if (isRendaFixa(produto)) {
            log.info("Renda Fixa: {}", produto);
            return extrairTickerRendaFixa(produto);
        } else { // Renda variável
            log.info("Renda Variável: {}", produto);
            return extrairTickerRendaVariavel(produto);
        }
    }

    /**
     * Métod privado para extrair o ticker de ativos de renda fixa (exceto Tesouro).
     * Concatena os dois primeiros tokens.
     * Exemplo: "CDB - CDBC247FRL8 - MERCADO CREDITO ..." retorna "CDB - CDBC247FRL8"
     */
    private String extrairTickerRendaFixa(String produto) {
        String[] partes = produto.split(" - ");
        if (partes.length >= 2) {
            return partes[0].trim() + " - " + partes[1].trim();
        }
        return produto.trim();
    }

    /**
     * Métod privado para extrair o ticker de ativos de renda variável.
     * Extrai o primeiro token e, se possível, valida que ele contenha 4 letras seguidas de 1 ou 2 números.
     * Exemplo: "BRCO11 - FII BRESCO" retorna "BRCO11"
     */
    private String extrairTickerRendaVariavel(String produto) {
        String[] partes = produto.split(" - ");
        if (partes.length > 0) {
            String candidate = partes[0].trim();
            Pattern pattern = Pattern.compile("([A-Z]{4}\\d{1,2})");
            Matcher matcher = pattern.matcher(candidate);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return candidate;
            }
        }
        return "";
    }

    @Override
    public boolean isRendaFixa(String produto) {
        if (produto == null || produto.trim().isEmpty()) {
            return false;
        }
        return RENDA_FIXA_PATTERN.matcher(produto).find();
    }

    /**
     * Extrai o código do ativo para renda fixa.
     * Se o produto começar com "TESOURO", retorna o produto completo;
     * caso contrário, retorna o segundo token.
     */
    @Override
    public String extrairCodigoRendaFixa(String produto) {
        if (produto == null || produto.isEmpty()) {
            return "";
        }
        String produtoTrim = produto.trim();
        if (produtoTrim.toUpperCase().startsWith("TESOURO")) {
            return produtoTrim;
        }
        String[] partes = produtoTrim.split(" - ");
        if (partes.length >= 2) {
            return partes[1].trim();
        }
        return "";
    }

    /**
     * Retorna o nome completo do ativo conforme informado, sem processamento adicional.
     */
    @Override
    public String extrairNomeCompleto(String produto) {
        return produto == null ? "" : produto.trim();
    }
}
