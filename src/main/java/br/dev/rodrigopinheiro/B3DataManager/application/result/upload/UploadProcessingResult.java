package br.dev.rodrigopinheiro.B3DataManager.application.result.upload;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Resultado do processamento de upload de arquivos Excel.
 * 
 * <p>Encapsula todas as informações sobre o resultado do processamento
 * de um arquivo Excel enviado via upload, incluindo estatísticas,
 * erros encontrados e relatório de erros gerado.</p>
 * 
 * <h3>Informações Fornecidas:</h3>
 * <ul>
 *   <li><strong>hasErrors:</strong> Indica se houve erros durante o processamento</li>
 *   <li><strong>errors:</strong> Lista detalhada de erros com dados originais</li>
 *   <li><strong>processedRows:</strong> Total de linhas processadas (excluindo cabeçalho)</li>
 *   <li><strong>successfulRows:</strong> Número de linhas processadas com sucesso</li>
 *   <li><strong>errorReportStream:</strong> Stream do relatório Excel com erros (se houver)</li>
 * </ul>
 * 
 * <h3>Cenários de Uso:</h3>
 * <ul>
 *   <li><strong>Processamento perfeito:</strong> hasErrors = false, errorReportStream = null</li>
 *   <li><strong>Processamento com erros:</strong> hasErrors = true, errorReportStream disponível</li>
 *   <li><strong>Falha total:</strong> successfulRows = 0, todos os erros coletados</li>
 * </ul>
 * 
 * <h3>Benefícios:</h3>
 * <ul>
 *   <li>Resultado estruturado e tipado</li>
 *   <li>Facilita tratamento na camada de apresentação</li>
 *   <li>Inclui relatório de erros pronto para download</li>
 *   <li>Estatísticas completas do processamento</li>
 * </ul>
 * 
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * UploadProcessingResult result = processUploadUseCase.execute(command);
 * 
 * if (result.hasErrors()) {
 *     // Criar link de download do relatório
 *     createDownloadLink(result.errorReportStream());
 *     showWarning("Processamento com " + result.errors().size() + " erros");
 * } else {
 *     showSuccess("Arquivo processado com sucesso!");
 * }
 * }</pre>
 * 
 * @param hasErrors Indica se houve erros durante o processamento
 * @param errors Lista de erros encontrados com dados originais
 * @param processedRows Número total de linhas processadas
 * @param successfulRows Número de linhas processadas com sucesso
 * @param errorReportStream Stream do arquivo Excel com relatório de erros
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record UploadProcessingResult(
    boolean hasErrors,
    List<ExcelRowError> errors,
    int processedRows,
    int successfulRows,
    ByteArrayInputStream errorReportStream
) {
    
    /**
     * Construtor que valida a consistência dos dados.
     */
    public UploadProcessingResult {
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
            throw new IllegalArgumentException("Linhas com sucesso não pode ser maior que linhas processadas");
        }
        
        // Validação de consistência entre hasErrors e errors
        if (hasErrors && errors.isEmpty()) {
            throw new IllegalArgumentException("Se hasErrors é true, deve haver erros na lista");
        }
        
        if (!hasErrors && !errors.isEmpty()) {
            throw new IllegalArgumentException("Se hasErrors é false, lista de erros deve estar vazia");
        }
        
        // Validação de consistência do errorReportStream
        if (hasErrors && errorReportStream == null) {
            throw new IllegalArgumentException("Se há erros, deve haver um relatório de erros");
        }
    }
    
    /**
     * Retorna o número de linhas com erro.
     * 
     * @return Número de linhas que apresentaram erro
     */
    public int getErrorRows() {
        return errors.size();
    }
    
    /**
     * Calcula a taxa de sucesso do processamento.
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
     * Verifica se o processamento foi completamente bem-sucedido.
     * 
     * @return true se todas as linhas foram processadas sem erro
     */
    public boolean isCompleteSuccess() {
        return !hasErrors && successfulRows == processedRows;
    }
    
    /**
     * Verifica se há relatório de erros disponível para download.
     * 
     * @return true se há relatório disponível
     */
    public boolean hasErrorReport() {
        return errorReportStream != null;
    }
    
    /**
     * Cria um resultado de sucesso completo (sem erros).
     * 
     * @param processedRows Número de linhas processadas
     * @return Resultado indicando sucesso total
     */
    public static UploadProcessingResult success(int processedRows) {
        return new UploadProcessingResult(
            false,
            List.of(),
            processedRows,
            processedRows,
            null
        );
    }
    
    /**
     * Cria um resultado com erros.
     * 
     * @param errors Lista de erros encontrados
     * @param processedRows Número total de linhas processadas
     * @param successfulRows Número de linhas processadas com sucesso
     * @param errorReportStream Stream do relatório de erros
     * @return Resultado com informações de erro
     */
    public static UploadProcessingResult withErrors(
            List<ExcelRowError> errors,
            int processedRows,
            int successfulRows,
            ByteArrayInputStream errorReportStream) {
        return new UploadProcessingResult(
            true,
            errors,
            processedRows,
            successfulRows,
            errorReportStream
        );
    }
    
    /**
     * Retorna uma representação textual do resultado.
     * 
     * @return String descrevendo o resultado do processamento
     */
    @Override
    public String toString() {
        return String.format(
            "UploadProcessingResult{hasErrors=%s, processedRows=%d, successfulRows=%d, errorCount=%d, successRate=%.1f%%}",
            hasErrors, processedRows, successfulRows, getErrorRows(), getSuccessRate()
        );
    }
}