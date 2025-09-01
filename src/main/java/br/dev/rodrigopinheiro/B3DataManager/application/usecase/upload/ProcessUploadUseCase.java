package br.dev.rodrigopinheiro.B3DataManager.application.usecase.upload;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.GenerateErrorReportCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ImportExcelCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.upload.ProcessUploadCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ImportExcelResult;
import br.dev.rodrigopinheiro.B3DataManager.application.result.upload.UploadProcessingResult;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.GenerateErrorReportUseCase;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.ImportExcelUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.ExcelProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

/**
 * Use Case responsável por processar uploads de arquivos Excel.
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Orquestrar o processamento de arquivos Excel enviados via upload</li>
 *   <li>Validar dados do comando de upload</li>
 *   <li>Delegar importação para ImportExcelUseCase</li>
 *   <li>Gerar relatório de erros quando necessário</li>
 *   <li>Retornar resultado estruturado para a camada de apresentação</li>
 * </ul>
 * 
 * <h3>Fluxo de Processamento:</h3>
 * <ol>
 *   <li>Validação dos dados de entrada</li>
 *   <li>Criação do comando de importação Excel</li>
 *   <li>Execução da importação via ImportExcelUseCase</li>
 *   <li>Geração de relatório de erros (se houver)</li>
 *   <li>Retorno do resultado estruturado</li>
 * </ol>
 * 
 * <h3>Tratamento de Erros:</h3>
 * <ul>
 *   <li>Validação de entrada: IllegalArgumentException</li>
 *   <li>Erro de processamento: ExcelProcessingException</li>
 *   <li>Erro inesperado: RuntimeException com log detalhado</li>
 * </ul>
 * 
 * <h3>Benefícios da Separação:</h3>
 * <ul>
 *   <li>Desacopla a View da lógica de processamento</li>
 *   <li>Facilita testes unitários isolados</li>
 *   <li>Permite reutilização em diferentes interfaces</li>
 *   <li>Centraliza tratamento de erros de upload</li>
 *   <li>Segue princípios da Arquitetura Hexagonal</li>
 * </ul>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Slf4j
@Component
public class ProcessUploadUseCase {
    
    private final ImportExcelUseCase importExcelUseCase;
    private final GenerateErrorReportUseCase generateErrorReportUseCase;
    
    public ProcessUploadUseCase(ImportExcelUseCase importExcelUseCase,
                               GenerateErrorReportUseCase generateErrorReportUseCase) {
        this.importExcelUseCase = importExcelUseCase;
        this.generateErrorReportUseCase = generateErrorReportUseCase;
    }
    
    /**
     * Executa o processamento de upload de arquivo Excel.
     * 
     * @param command Comando contendo dados do upload
     * @return Resultado estruturado do processamento
     * @throws IllegalArgumentException se os dados de entrada forem inválidos
     * @throws ExcelProcessingException se houver erro no processamento do Excel
     */
    @Transactional(timeout = 300) // 5 minutos para uploads grandes
    public UploadProcessingResult execute(ProcessUploadCommand command) {
        log.info("Iniciando processamento de upload. Arquivo: {}, Usuário: {}, Tamanho: {} bytes",
                command.fileName(), command.usuarioId().value(), command.fileBytes().length);
        
        try {
            // Validação de entrada
            validateCommand(command);
            
            // Criar comando de importação Excel
            ImportExcelCommand importCommand = new ImportExcelCommand(
                new ByteArrayInputStream(command.fileBytes()),
                command.usuarioId()
            );
            
            // Executar importação
            ImportExcelResult importResult = importExcelUseCase.execute(importCommand);
            
            log.info("Importação concluída. Processadas: {}, Sucessos: {}, Erros: {}",
                    importResult.processedRows(), importResult.successfulRows(), importResult.errors().size());
            
            // Gerar relatório de erros se necessário
            ByteArrayInputStream errorReportStream = null;
            if (importResult.hasErrors()) {
                log.info("Gerando relatório de erros para {} linhas com problema", importResult.errors().size());
                
                GenerateErrorReportCommand reportCommand = new GenerateErrorReportCommand(importResult.errors());
                errorReportStream = generateErrorReportUseCase.execute(reportCommand);
                
                log.info("Relatório de erros gerado com sucesso");
            }
            
            // Retornar resultado estruturado
            return new UploadProcessingResult(
                importResult.hasErrors(),
                importResult.errors(),
                importResult.processedRows(),
                importResult.successfulRows(),
                errorReportStream
            );
            
        } catch (IllegalArgumentException e) {
            log.error("Dados de entrada inválidos: {}", e.getMessage());
            throw e;
            
        } catch (ExcelProcessingException e) {
            log.error("Erro no processamento do arquivo Excel: {}", e.getMessage(), e);
            throw e;
            
        } catch (Exception e) {
            log.error("Erro inesperado durante processamento de upload: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Erro inesperado durante processamento: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida os dados do comando de entrada.
     * 
     * @param command Comando a ser validado
     * @throws IllegalArgumentException se algum dado for inválido
     */
    private void validateCommand(ProcessUploadCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }
        
        if (command.fileBytes() == null || command.fileBytes().length == 0) {
            throw new IllegalArgumentException("Arquivo não pode estar vazio");
        }
        
        if (command.fileName() == null || command.fileName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo é obrigatório");
        }
        
        if (command.usuarioId() == null || command.usuarioId().value() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
        
        // Validação de tamanho máximo (50MB)
        if (command.fileBytes().length > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("Arquivo muito grande. Tamanho máximo: 50MB");
        }
        
        // Validação de extensão
        if (!command.fileName().toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Apenas arquivos .xlsx são suportados");
        }
        
        log.debug("Validação do comando concluída com sucesso");
    }
}