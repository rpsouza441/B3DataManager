package br.dev.rodrigopinheiro.B3DataManager.application.service.formatter;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Serviço responsável por formatação de dados de operações financeiras.
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Formatação de valores monetários</li>
 *   <li>Formatação de preços unitários</li>
 *   <li>Formatação de quantidades</li>
 *   <li>Aplicação de regras de negócio para exibição</li>
 * </ul>
 * 
 * <h3>Regras de Formatação:</h3>
 * <ul>
 *   <li><strong>Operações sem quantidade (0):</strong> Exibe "-" para valores e preços</li>
 *   <li><strong>Operações gratuitas:</strong> Exibe "R$ 0,00" para valores zero com quantidade > 0</li>
 *   <li><strong>Valores normais:</strong> Formato "R$ X,XX" com 2 casas decimais</li>
 *   <li><strong>Preços:</strong> Formato "R$ X,XXX" com 3 casas decimais</li>
 *   <li><strong>Quantidades:</strong> Remove zeros desnecessários e evita notação científica</li>
 * </ul>
 * 
 * <h3>Casos de Uso:</h3>
 * <ul>
 *   <li>Direitos de subscrição não exercidos (quantidade = 0)</li>
 *   <li>Atualizações de posição (quantidade = 0)</li>
 *   <li>Operações gratuitas (valor = 0, quantidade > 0)</li>
 *   <li>Operações normais com valores e quantidades</li>
 * </ul>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Service
public class OperacaoFormatterService {
    
    /**
     * Formata valores monetários seguindo regras de negócio.
     * 
     * @param valor Valor a ser formatado
     * @param quantidade Quantidade da operação (determina se deve mostrar "-")
     * @return String formatada do valor
     */
    public String formatarValor(BigDecimal valor, BigDecimal quantidade) {
        if (valor == null) {
            return "-";
        }
        
        // Para operações sem quantidade (direitos não exercidos, atualizações, etc.)
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
            return "-";
        }
        
        // Para operações com valor zero mas quantidade > 0 (operações gratuitas)
        if (valor.compareTo(BigDecimal.ZERO) == 0) {
            return "R$ 0,00";
        }
        
        // Para operações com valor normal
        return String.format("R$ %.2f", valor);
    }
    
    /**
     * Formata preços unitários seguindo regras de negócio.
     * 
     * @param preco Preço unitário a ser formatado
     * @param quantidade Quantidade da operação (determina se deve mostrar "-")
     * @return String formatada do preço
     */
    public String formatarPreco(BigDecimal preco, BigDecimal quantidade) {
        if (preco == null) {
            return "-";
        }
        
        // Para operações sem quantidade (direitos não exercidos, atualizações, etc.)
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
            return "-";
        }
        
        // Para operações com preço normal (3 casas decimais para maior precisão)
        return String.format("R$ %.3f", preco);
    }
    
    /**
     * Formata quantidades evitando notação científica.
     * 
     * @param quantidade Quantidade a ser formatada
     * @return String formatada da quantidade
     */
    public String formatarQuantidade(BigDecimal quantidade) {
        if (quantidade == null) {
            return "0";
        }
        
        // Para quantidade zero, mostrar simplesmente "0"
        if (quantidade.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        
        // Para quantidades normais, usar formatação sem notação científica
        // Remove zeros desnecessários à direita
        return quantidade.stripTrailingZeros().toPlainString();
    }
    
    /**
     * Formata diferença de valores (usado para destacar discrepâncias).
     * 
     * @param diferenca Valor da diferença
     * @return String formatada da diferença ou string vazia se zero
     */
    public String formatarDiferenca(BigDecimal diferenca) {
        if (diferenca == null || diferenca.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        
        return String.format("R$ %.2f", diferenca);
    }
    
    /**
     * Verifica se uma operação deve exibir valores monetários.
     * 
     * @param quantidade Quantidade da operação
     * @return true se deve exibir valores, false se deve exibir "-"
     */
    public boolean deveExibirValores(BigDecimal quantidade) {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }
}