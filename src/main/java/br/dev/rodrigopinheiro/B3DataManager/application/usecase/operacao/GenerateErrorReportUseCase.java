package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.GenerateErrorReportCommand;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.ExcelProcessingException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Use Case responsável por gerar relatórios de erro de importação Excel.
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Gerar arquivo Excel com linhas que apresentaram erro</li>
 *   <li>Incluir dados originais para facilitar correção</li>
 *   <li>Formatar adequadamente o relatório para legibilidade</li>
 *   <li>Otimizar layout com auto-ajuste de colunas</li>
 * </ul>
 * 
 * <h3>Estrutura do Relatório:</h3>
 * <ul>
 *   <li>Primeira coluna: "ERRO" - Mensagem descritiva do problema</li>
 *   <li>Demais colunas: Dados originais da linha (Data, Produto, etc.)</li>
 *   <li>Cabeçalho: Nomes das colunas para identificação</li>
 *   <li>Formatação: Auto-ajuste de largura para melhor visualização</li>
 * </ul>
 * 
 * <h3>Benefícios para o Usuário:</h3>
 * <ul>
 *   <li>Visualização clara dos erros encontrados</li>
 *   <li>Dados originais preservados para correção</li>
 *   <li>Possibilidade de corrigir diretamente no arquivo</li>
 *   <li>Reimportação simples após correções</li>
 *   <li>Redução significativa do tempo de correção</li>
 * </ul>
 * 
 * <h3>Tratamento de Erros:</h3>
 * <ul>
 *   <li>Erro na criação do arquivo: ExcelProcessingException</li>
 *   <li>Erro de I/O: ExcelProcessingException com causa específica</li>
 *   <li>Dados inconsistentes: IllegalArgumentException</li>
 *   <li>Erro inesperado: RuntimeException com log detalhado</li>
 * </ul>
 * 
 * <h3>Performance:</h3>
 * <ul>
 *   <li>Geração em memória para arquivos pequenos/médios</li>
 *   <li>Auto-ajuste de colunas otimizado</li>
 *   <li>Estrutura de dados eficiente para grandes volumes</li>
 *   <li>Log de progresso para relatórios grandes</li>
 * </ul>
 * 
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * List<ExcelRowError> errors = Arrays.asList(
 *     new ExcelRowError(5, "Data inválida", Map.of(
 *         "Data", "32/13/2025",
 *         "Produto", "PETR4",
 *         "Quantidade", "100"
 *     )),
 *     new ExcelRowError(12, "Quantidade deve ser > 0", Map.of(
 *         "Data", "15/08/2025",
 *         "Produto", "VALE3",
 *         "Quantidade", "-50"
 *     ))
 * );
 * 
 * GenerateErrorReportCommand command = new GenerateErrorReportCommand(errors);
 * ByteArrayInputStream reportStream = generateErrorReportUseCase.execute(command);
 * 
 * // Disponibilizar para download
 * return new StreamResource("relatorio_erros.xlsx", () -> reportStream);
 * }</pre>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Slf4j
@Component
public class GenerateErrorReportUseCase {
    
    private static final String ERROR_COLUMN_HEADER = "ERRO";
    private static final String SHEET_NAME = "Linhas com Erro";
    
    /**
     * Executa a geração do relatório de erros.
     * 
     * @param command Comando contendo os erros a serem reportados
     * @return Stream do arquivo Excel gerado
     * @throws ExcelProcessingException se houver erro na geração do arquivo
     * @throws IllegalArgumentException se os dados do comando forem inválidos
     */
    public ByteArrayInputStream execute(GenerateErrorReportCommand command) {
        log.info("Iniciando geração de relatório de erros. Total de erros: {}", command.getErrorCount());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            
            // Coleta todas as colunas únicas dos erros
            Set<String> allColumns = collectAllColumns(command.errors());
            log.debug("Colunas identificadas no relatório: {}", allColumns);
            
            // Cria cabeçalho
            createHeader(sheet, allColumns);
            
            // Adiciona dados dos erros
            populateErrorData(sheet, command.errors(), allColumns);
            
            // Ajusta largura das colunas
            autoSizeColumns(sheet, allColumns.size() + 1); // +1 para coluna de erro
            
            // Converte para ByteArrayInputStream
            ByteArrayInputStream result = convertToInputStream(workbook);
            
            log.info("Relatório de erros gerado com sucesso. {} linhas de erro processadas", 
                    command.getErrorCount());
            
            return result;
            
        } catch (IOException e) {
            log.error("Erro de I/O durante geração do relatório: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Erro ao gerar relatório de erros: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado durante geração do relatório: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao gerar relatório de erros", e);
        }
    }
    
    /**
     * Coleta todas as colunas únicas presentes nos erros.
     */
    private Set<String> collectAllColumns(java.util.List<ExcelRowError> errors) {
        Set<String> allColumns = new LinkedHashSet<>();
        
        for (ExcelRowError error : errors) {
            allColumns.addAll(error.originalData().keySet());
        }
        
        log.debug("Total de colunas únicas encontradas: {}", allColumns.size());
        return allColumns;
    }
    
    /**
     * Cria o cabeçalho do relatório.
     */
    private void createHeader(Sheet sheet, Set<String> columns) {
        Row headerRow = sheet.createRow(0);
        
        // Primeira coluna: ERRO
        headerRow.createCell(0).setCellValue(ERROR_COLUMN_HEADER);
        
        // Demais colunas: dados originais
        int columnIndex = 1;
        for (String columnName : columns) {
            headerRow.createCell(columnIndex++).setCellValue(columnName);
        }
        
        log.debug("Cabeçalho criado com {} colunas", columns.size() + 1);
    }
    
    /**
     * Popula os dados dos erros no relatório.
     */
    private void populateErrorData(Sheet sheet, java.util.List<ExcelRowError> errors, Set<String> columns) {
        int rowIndex = 1; // Começa após o cabeçalho
        
        for (ExcelRowError error : errors) {
            Row dataRow = sheet.createRow(rowIndex++);
            
            // Primeira coluna: mensagem de erro
            dataRow.createCell(0).setCellValue(error.errorMessage());
            
            // Demais colunas: dados originais
            int columnIndex = 1;
            for (String columnName : columns) {
                String value = error.originalData().getOrDefault(columnName, "");
                dataRow.createCell(columnIndex++).setCellValue(value);
            }
            
            // Log de progresso para relatórios grandes
            if (rowIndex % 100 == 0) {
                log.debug("Processadas {} linhas de erro", rowIndex - 1);
            }
        }
        
        log.debug("Dados populados: {} linhas de erro", errors.size());
    }
    
    /**
     * Ajusta automaticamente a largura das colunas.
     */
    private void autoSizeColumns(Sheet sheet, int totalColumns) {
        try {
            for (int i = 0; i < totalColumns; i++) {
                sheet.autoSizeColumn(i);
                
                // Limita largura máxima para evitar colunas muito largas
                int currentWidth = sheet.getColumnWidth(i);
                int maxWidth = 15000; // Aproximadamente 15 caracteres
                if (currentWidth > maxWidth) {
                    sheet.setColumnWidth(i, maxWidth);
                }
            }
            log.debug("Auto-ajuste aplicado a {} colunas", totalColumns);
        } catch (Exception e) {
            log.warn("Erro durante auto-ajuste de colunas: {}. Continuando sem ajuste.", e.getMessage());
        }
    }
    
    /**
     * Converte o workbook para ByteArrayInputStream.
     */
    private ByteArrayInputStream convertToInputStream(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            
            log.debug("Arquivo Excel gerado. Tamanho: {} bytes", bytes.length);
            return new ByteArrayInputStream(bytes);
        }
    }
}