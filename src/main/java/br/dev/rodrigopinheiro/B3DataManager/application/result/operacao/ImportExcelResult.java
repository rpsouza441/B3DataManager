package br.dev.rodrigopinheiro.B3DataManager.application.result.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;
import java.util.List;

/**
 * Resultado da importação de operações a partir de arquivo Excel.
 * 
 * <p>Este resultado encapsula as informações sobre o processamento do arquivo Excel,
 * incluindo estatísticas de processamento e detalhes sobre erros encontrados.</p>
 * 
 * <p>Informações fornecidas:</p>
 * <ul>
 *   <li><strong>Erros:</strong> Lista de erros encontrados durante o processamento</li>
 *   <li><strong>Linhas processadas:</strong> Total de linhas lidas do arquivo (excluindo cabeçalho)</li>
 *   <li><strong>Linhas com sucesso:</strong> Número de operações importadas com sucesso</li>
 *   <li><strong>Taxa de sucesso:</strong> Percentual de linhas processadas com sucesso</li>
 * </ul>
 * 
 * <p>Cenários de uso:</p>
 * <ul>
 *   <li><strong>Importação perfeita:</strong> errors vazio, processedRows = successfulRows</li>
 *   <li><strong>Importação com erros:</strong> errors não vazio, successfulRows < processedRows</li>
 *   <li><strong>Falha total:</strong> successfulRows = 0, errors com todas as linhas</li>
 * </ul>
 * 
 * <p>Os erros são formatados para facilitar a correção:</p>
 * <ul>
 *   <li>Incluem número da linha onde ocorreu o erro</li>
 *   <li>Descrevem claramente o problema encontrado</li>
 *   <li>Podem ser usados para gerar relatório de erros</li>
 * </ul>
 * 
 * @param errors Lista de erros encontrados durante o processamento (com dados originais)
 * @param processedRows Número total de linhas processadas (excluindo cabeçalho)
 * @param successfulRows Número de linhas processadas com sucesso
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record ImportExcelResult(
    List<ExcelRowError> errors,
    int processedRows,
    int successfulRows
) {
    
    /**
     * Construtor que valida a consistência dos dados.
     */
    public ImportExcelResult {
        if (errors == null) {
            throw new IllegalArgumentException("Lista de erros não pode ser nula");
        }
        if (processedRows < 0) {
            throw new IllegalArgumentException("Número de linhas processadas não pode ser negativo");
        }
        if (successfulRows < 0) {
            throw new IllegalArgumentException("Número de linhas com sucesso não pode ser negativo");
        }
        if (successfulRows > processedRows) {
            throw new IllegalArgumentException("Número de linhas com sucesso não pode ser maior que linhas processadas");
        }
    }
    
    /**
     * Verifica se houve erros durante a importação.
     * 
     * @return true se houve pelo menos um erro
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Calcula o número de linhas com erro.
     * 
     * @return Número de linhas que falharam no processamento
     */
    public int getErrorRows() {
        return processedRows - successfulRows;
    }
    
    /**
     * Calcula a taxa de sucesso da importação.
     * 
     * @return Percentual de sucesso (0.0 a 100.0)
     */
    public double getSuccessRate() {
        if (processedRows == 0) {
            return 100.0;
        }
        return (double) successfulRows / processedRows * 100.0;
    }
    
    /**
     * Verifica se a importação foi completamente bem-sucedida.
     * 
     * @return true se todas as linhas foram processadas sem erro
     */
    public boolean isCompleteSuccess() {
        return errors.isEmpty() && successfulRows == processedRows;
    }
    
    /**
     * Cria um resultado de sucesso completo (sem erros).
     * 
     * @param processedRows Número de linhas processadas
     * @return Resultado indicando sucesso total
     */
    public static ImportExcelResult success(int processedRows) {
        return new ImportExcelResult(List.of(), processedRows, processedRows);
    }
    
    /**
     * Cria um resultado com erros.
     * 
     * @param errors Lista de erros encontrados
     * @param processedRows Número total de linhas processadas
     * @param successfulRows Número de linhas processadas com sucesso
     * @return Resultado com informações de erro
     */
    public static ImportExcelResult withErrors(List<ExcelRowError> errors, int processedRows, int successfulRows) {
        return new ImportExcelResult(errors, processedRows, successfulRows);
    }
}