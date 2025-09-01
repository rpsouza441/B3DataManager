package br.dev.rodrigopinheiro.B3DataManager.infrastructure.excel;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.GenerateErrorReportCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ImportExcelCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ImportExcelResult;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.GenerateErrorReportUseCase;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.ImportExcelUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.ExcelProcessingException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador que simplifica o uso do ImportExcelUseCase.
 * 
 * <p>Mantém compatibilidade com a interface existente enquanto
 * delega o processamento real para a arquitetura hexagonal.</p>
 */
@Slf4j
@Service
public class ExcelFileImporter {

    private final ImportExcelUseCase importExcelUseCase;
    private final GenerateErrorReportUseCase generateErrorReportUseCase;

    public ExcelFileImporter(
            ImportExcelUseCase importExcelUseCase,
            GenerateErrorReportUseCase generateErrorReportUseCase) {
        this.importExcelUseCase = importExcelUseCase;
        this.generateErrorReportUseCase = generateErrorReportUseCase;
    }

    /**
     * Processa o arquivo Excel usando a arquitetura hexagonal.
     *
     * @param inputStream Fluxo de entrada do arquivo Excel
     * @param userId      ID do usuário logado
     * @param locale      Localização (mantido para compatibilidade, mas não usado)
     * @return Lista de mensagens de erro por linha
     */
    public List<String> processFile(InputStream inputStream, Long userId, Locale locale) {
        log.info("Iniciando processamento de arquivo Excel para usuário: {}", userId);
        
        try {
            // Criar comando para o Use Case
            ImportExcelCommand command = new ImportExcelCommand(
                inputStream,
                new UsuarioId(userId)
            );
            
            // Executar importação usando arquitetura hexagonal
            ImportExcelResult result = importExcelUseCase.execute(command);
            
            log.info("Processamento concluído. Linhas processadas: {}, Sucessos: {}, Erros: {}",
                    result.processedRows(), result.successfulRows(), result.errors().size());
            
            // Converte ExcelRowError para String para compatibilidade
            return result.errors().stream()
                    .map(error -> String.format("Linha %d: %s", error.rowNumber(), error.errorMessage()))
                    .toList();
            
        } catch (Exception e) {
            log.error("Erro durante processamento do arquivo Excel: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Erro ao processar arquivo Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Gera arquivo Excel com erros de importação.
     * 
     * @param errors Lista de mensagens de erro
     * @param locale Localização (mantido para compatibilidade)
     * @return Stream do arquivo Excel gerado
     */
    public ByteArrayInputStream generateErrorFile(List<String> errors, Locale locale) {
        log.info("Gerando arquivo de erros com {} entradas", errors.size());
        
        try {
            // Busca os ExcelRowError originais do último processamento
            // Para isso, precisamos reprocessar ou manter cache dos erros
            // Por enquanto, vamos usar uma abordagem simplificada
            
            List<ExcelRowError> excelRowErrors = new ArrayList<>();
            
            for (String error : errors) {
                // Parse da string "Linha X: mensagem" para extrair número da linha
                int lineNumber = 1;
                String message = error;
                
                if (error.startsWith("Linha ")) {
                    try {
                        int colonIndex = error.indexOf(":");
                        if (colonIndex > 0) {
                            String lineStr = error.substring(6, colonIndex).trim();
                            lineNumber = Integer.parseInt(lineStr);
                            message = error.substring(colonIndex + 1).trim();
                        }
                    } catch (NumberFormatException e) {
                        // Se não conseguir parsear, usa valores padrão
                        lineNumber = 1;
                        message = error;
                    }
                }
                
                // Cria ExcelRowError com dados mínimos
                // TODO: Implementar cache de dados originais para melhor experiência
                ExcelRowError rowError = new ExcelRowError(
                    lineNumber,
                    message,
                    java.util.Map.of("Erro", error) // Dados mínimos
                );
                excelRowErrors.add(rowError);
            }
            
            // Usa GenerateErrorReportUseCase para gerar Excel real
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(excelRowErrors);
            return generateErrorReportUseCase.execute(command);
            
        } catch (Exception e) {
            log.error("Erro ao gerar arquivo de erros: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Erro ao gerar arquivo de erros: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gera um arquivo Excel com dados completos das linhas de erro.
     * 
     * <p>Esta é a versão melhorada que inclui os dados originais das linhas
     * para facilitar a correção pelo usuário.</p>
     *
     * @param excelRowErrors Lista de erros com dados originais das linhas
     * @return Fluxo de entrada do arquivo Excel gerado
     */
    public ByteArrayInputStream generateErrorFileWithOriginalData(List<ExcelRowError> excelRowErrors) {
        log.info("Gerando arquivo de erros completo com {} entradas", excelRowErrors.size());
        
        try {
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(excelRowErrors);
            return generateErrorReportUseCase.execute(command);
            
        } catch (Exception e) {
            log.error("Erro ao gerar arquivo de erros completo: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Erro ao gerar arquivo de erros: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extrai o número da linha da mensagem de erro.
     */
    private int extractRowNumber(String errorMessage, int defaultRowNumber) {
        try {
            // Tenta extrair número da linha do formato "Linha X: erro"
            if (errorMessage.startsWith("Linha ")) {
                int colonIndex = errorMessage.indexOf(":");
                if (colonIndex > 6) {
                    String rowNumberStr = errorMessage.substring(6, colonIndex).trim();
                    return Integer.parseInt(rowNumberStr);
                }
            }
        } catch (Exception e) {
            log.debug("Não foi possível extrair número da linha de: {}", errorMessage);
        }
        
        return defaultRowNumber;
    }
}
