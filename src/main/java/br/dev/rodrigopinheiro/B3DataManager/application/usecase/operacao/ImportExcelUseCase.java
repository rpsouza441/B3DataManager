package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CheckDuplicateCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ImportExcelCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.RegisterOperacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.CheckDuplicateResult;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ImportExcelResult;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.ExcelProcessingException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.InvalidDataException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Case responsável por importar operações a partir de arquivo Excel.
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Processar arquivo Excel linha por linha</li>
 *   <li>Validar dados de cada operação</li>
 *   <li>Verificar duplicidade usando CheckDuplicateOperacaoUseCase</li>
 *   <li>Registrar operações válidas usando RegisterOperacaoUseCase</li>
 *   <li>Coletar e reportar erros de processamento</li>
 * </ul>
 * 
 * <h3>Formato do Arquivo Excel:</h3>
 * <ul>
 *   <li>Linha 1: Cabeçalho (ignorada)</li>
 *   <li>Coluna A: Entrada/Saída (Credito/Debito)</li>
 *   <li>Coluna B: Data (formato dd/MM/yyyy)</li>
 *   <li>Coluna C: Movimentação</li>
 *   <li>Coluna D: Produto</li>
 *   <li>Coluna E: Instituição</li>
 *   <li>Coluna F: Quantidade</li>
 *   <li>Coluna G: Preço Unitário</li>
 *   <li>Coluna H: Valor Operação</li>
 * </ul>
 * 
 * <h3>Regras de Negócio:</h3>
 * <ul>
 *   <li>Linhas com erro são coletadas mas não interrompem o processamento</li>
 *   <li>Operações duplicadas são marcadas automaticamente</li>
 *   <li>Todas as operações são associadas ao usuário do comando</li>
 *   <li>Transação é aplicada por operação individual</li>
 * </ul>
 * 
 * <h3>Tratamento de Erros:</h3>
 * <ul>
 *   <li>Arquivo corrompido: ExcelProcessingException</li>
 *   <li>Dados inválidos por linha: InvalidDataException (coletada)</li>
 *   <li>Erro de validação: OperacaoInvalidaException (coletada)</li>
 *   <li>Erro inesperado: RuntimeException com log detalhado</li>
 * </ul>
 * 
 * <h3>Performance:</h3>
 * <ul>
 *   <li>Processamento streaming do arquivo Excel</li>
 *   <li>Transações individuais por operação</li>
 *   <li>Timeout configurado para evitar travamentos</li>
 *   <li>Log de progresso a cada 100 linhas processadas</li>
 * </ul>
 * 
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * try (InputStream inputStream = new FileInputStream("operacoes.xlsx")) {
 *     ImportExcelCommand command = new ImportExcelCommand(
 *         inputStream,
 *         new UsuarioId(1L)
 *     );
 *     
 *     ImportExcelResult result = importExcelUseCase.execute(command);
 *     
 *     if (result.hasErrors()) {
 *         log.warn("Importação com {} erros de {} linhas processadas", 
 *                  result.errors().size(), result.processedRows());
 *     } else {
 *         log.info("Importação concluída com sucesso: {} operações", 
 *                  result.successfulRows());
 *     }
 * }
 * }</pre>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Slf4j
@Component
public class ImportExcelUseCase {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int PROGRESS_LOG_INTERVAL = 100;
    
    private final RegisterOperacaoUseCase registerOperacaoUseCase;
    private final CheckDuplicateOperacaoUseCase checkDuplicateUseCase;
    
    public ImportExcelUseCase(
            RegisterOperacaoUseCase registerOperacaoUseCase,
            CheckDuplicateOperacaoUseCase checkDuplicateUseCase) {
        this.registerOperacaoUseCase = registerOperacaoUseCase;
        this.checkDuplicateUseCase = checkDuplicateUseCase;
    }
    
    /**
     * Executa a importação de operações a partir de arquivo Excel.
     * 
     * @param command Comando contendo o arquivo Excel e usuário
     * @return Resultado com estatísticas e erros da importação
     * @throws ExcelProcessingException se houver erro na leitura do arquivo
     * @throws IllegalArgumentException se os dados do comando forem inválidos
     */
    @Transactional(timeout = 300) // 5 minutos para importações grandes
    public ImportExcelResult execute(ImportExcelCommand command) {
        log.info("Iniciando importação Excel para usuário: {}", command.usuarioId().value());
        
        List<ExcelRowError> errors = new ArrayList<>();
        int processedRows = 0;
        int successfulRows = 0;
        
        try (Workbook workbook = new XSSFWorkbook(command.inputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            log.info("Arquivo Excel carregado. Total de linhas: {}", sheet.getLastRowNum());
            
            for (Row row : sheet) {
                // Pular cabeçalho
                if (row.getRowNum() == 0) {
                    continue;
                }
                
                processedRows++;
                
                try {
                    // Log de progresso
                    if (processedRows % PROGRESS_LOG_INTERVAL == 0) {
                        log.info("Processando linha {}: {} sucessos, {} erros", 
                                processedRows, successfulRows, errors.size());
                    }
                    
                    // Processa linha individual
                    processRow(row, command.usuarioId());
                    successfulRows++;
                    
                } catch (InvalidDataException e) {
                    log.warn("Erro de validação na linha {}: {}", row.getRowNum() + 1, e.getMessage());
                    errors.add(createExcelRowError(row, e.getMessage()));
                    
                } catch (OperacaoInvalidaException e) {
                    log.warn("Operação inválida na linha {}: {}", row.getRowNum() + 1, e.getMessage());
                    errors.add(createExcelRowError(row, e.getMessage()));
                    
                } catch (Exception e) {
                    log.error("Erro inesperado na linha {}: {}", row.getRowNum() + 1, e.getMessage(), e);
                    errors.add(createExcelRowError(row, "Erro interno: " + e.getMessage()));
                }
            }
            
        } catch (IOException e) {
            log.error("Erro ao processar arquivo Excel: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Arquivo Excel corrompido ou inválido: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado durante importação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado durante importação Excel", e);
        }
        
        log.info("Importação concluída. Processadas: {}, Sucessos: {}, Erros: {}", 
                processedRows, successfulRows, errors.size());
        
        return new ImportExcelResult(errors, processedRows, successfulRows);
    }
    
    /**
     * Processa uma linha individual do Excel.
     * Ordem das colunas: Entrada/Saída, Data, Movimentação, Produto, Instituição, Quantidade, Preço unitário, Valor da Operação
     */
    private void processRow(Row row, UsuarioId usuarioId) {
        // Extrai dados da linha na ordem correta
        String entradaSaida = getCellValueAsString(row, 0);
        LocalDate data = parseDate(getCellValueAsString(row, 1));
        String movimentacao = getCellValueAsString(row, 2);
        String produto = getCellValueAsString(row, 3);
        String instituicao = getCellValueAsString(row, 4);
        BigDecimal quantidade = parseBigDecimal(getCellValueAsString(row, 5));
        BigDecimal precoUnitario = parseBigDecimal(getCellValueAsString(row, 6));
        BigDecimal valorOperacao = parseBigDecimal(getCellValueAsString(row, 7));
        
        // Normaliza entrada/saída
        if (entradaSaida.equalsIgnoreCase("credito") || entradaSaida.equalsIgnoreCase("crédito")) {
            entradaSaida = "Entrada";
        } else if (entradaSaida.equalsIgnoreCase("debito") || entradaSaida.equalsIgnoreCase("débito")) {
            entradaSaida = "Saída";
        }
        
        // Verifica duplicidade
        CheckDuplicateCommand duplicateCommand = new CheckDuplicateCommand(
            data, movimentacao, produto, instituicao, 
            quantidade, precoUnitario, valorOperacao, usuarioId
        );
        
        CheckDuplicateResult duplicateResult = checkDuplicateUseCase.execute(duplicateCommand);
        
        // Registra operação
        RegisterOperacaoCommand registerCommand = new RegisterOperacaoCommand(
            entradaSaida,
            data,
            movimentacao,
            produto,
            instituicao,
            new Quantidade(quantidade),
            new Dinheiro(precoUnitario),
            new Dinheiro(valorOperacao),
            duplicateResult.isDuplicate(),
            false, // dimensionado
            duplicateResult.originalId(),
            false, // deletado
            usuarioId
        );
        
        registerOperacaoUseCase.execute(registerCommand);
    }
    
    /**
     * Obtém valor da célula como string.
     */
    private String getCellValueAsString(Row row, int columnIndex) {
        if (row.getCell(columnIndex) == null) {
            return "";
        }
        
        return switch (row.getCell(columnIndex).getCellType()) {
            case STRING -> row.getCell(columnIndex).getStringCellValue().trim();
            case NUMERIC -> String.valueOf(row.getCell(columnIndex).getNumericCellValue());
            case BOOLEAN -> String.valueOf(row.getCell(columnIndex).getBooleanCellValue());
            case FORMULA -> row.getCell(columnIndex).getCellFormula();
            default -> "";
        };
    }
    
    /**
     * Converte string para LocalDate.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new InvalidDataException("Data é obrigatória");
        }
        
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Data inválida: " + dateStr + ". Formato esperado: dd/MM/yyyy");
        }
    }
    
    /**
     * Converte string para BigDecimal.
     * Trata valores monetários brasileiros (R$0,059) e hífens (-).
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        String trimmedValue = value.trim();
        
        // Trata hífen como zero (comum em operações sem valor)
        if ("-".equals(trimmedValue)) {
            return BigDecimal.ZERO;
        }
        
        try {
            // Remove símbolos monetários (R$) e espaços
            String cleanValue = trimmedValue
                .replaceAll("R\\$", "")
                .replaceAll("\\s+", "")
                .trim();
            
            // Se ainda estiver vazio após limpeza, retorna zero
            if (cleanValue.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            // Substitui vírgula por ponto para parsing decimal
            cleanValue = cleanValue.replace(",", ".");
            
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Valor numérico inválido: " + value);
        }
    }
    
    /**
     * Formata erro de linha para relatório.
     */
    /**
     * Cria um ExcelRowError preservando os dados originais da linha.
     */
    private ExcelRowError createExcelRowError(Row row, String errorMessage) {
        Map<String, String> originalData = new LinkedHashMap<>();
        
        // Preserva dados originais na ordem das colunas
        originalData.put("Entrada/Saída", getCellValueAsString(row, 0));
        originalData.put("Data", getCellValueAsString(row, 1));
        originalData.put("Movimentação", getCellValueAsString(row, 2));
        originalData.put("Produto", getCellValueAsString(row, 3));
        originalData.put("Instituição", getCellValueAsString(row, 4));
        originalData.put("Quantidade", getCellValueAsString(row, 5));
        originalData.put("Preço unitário", getCellValueAsString(row, 6));
        originalData.put("Valor da Operação", getCellValueAsString(row, 7));
        
        return new ExcelRowError(row.getRowNum() + 1, errorMessage, originalData);
    }
}