package br.dev.rodrigopinheiro.B3DataManager.application.result.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;

import java.util.List;

/**
 * Resultado da busca de operações para processamento em batch.
 * 
 * <p>Este resultado encapsula a lista de operações encontradas e
 * informações sobre a paginação para controle do processamento em lote.</p>
 * 
 * @param operacoes Lista de operações encontradas
 * @param totalElements Total de elementos disponíveis
 * @param hasNext Indica se há mais páginas disponíveis
 * @param currentPage Página atual (baseada em offset/pageSize)
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record GetOperacoesForBatchResult(
    List<Operacao> operacoes,
    long totalElements,
    boolean hasNext,
    int currentPage
) {
    
    /**
     * Construtor que valida os parâmetros.
     */
    public GetOperacoesForBatchResult {
        if (operacoes == null) {
            throw new IllegalArgumentException("Lista de operações não pode ser nula");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total de elementos não pode ser negativo");
        }
        if (currentPage < 0) {
            throw new IllegalArgumentException("Página atual não pode ser negativa");
        }
    }
    
    /**
     * Verifica se há operações no resultado.
     * 
     * @return true se há pelo menos uma operação
     */
    public boolean hasOperacoes() {
        return !operacoes.isEmpty();
    }
    
    /**
     * Retorna o número de operações na página atual.
     * 
     * @return Quantidade de operações
     */
    public int getPageSize() {
        return operacoes.size();
    }
    
    /**
     * Cria um resultado vazio.
     * 
     * @return Resultado sem operações
     */
    public static GetOperacoesForBatchResult empty() {
        return new GetOperacoesForBatchResult(List.of(), 0, false, 0);
    }
}