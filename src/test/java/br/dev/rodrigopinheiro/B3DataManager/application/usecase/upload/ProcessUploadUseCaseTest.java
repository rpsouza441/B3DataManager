package br.dev.rodrigopinheiro.B3DataManager.application.usecase.upload;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.GenerateErrorReportCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ImportExcelCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.upload.ProcessUploadCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ImportExcelResult;
import br.dev.rodrigopinheiro.B3DataManager.application.result.upload.UploadProcessingResult;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.GenerateErrorReportUseCase;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.ImportExcelUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.ExcelProcessingException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProcessUploadUseCase.
 * 
 * <p>Testa todos os cenários de processamento de upload,
 * incluindo sucessos, erros e validações.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessUploadUseCase")
class ProcessUploadUseCaseTest {
    
    @Mock
    private ImportExcelUseCase importExcelUseCase;
    
    @Mock
    private GenerateErrorReportUseCase generateErrorReportUseCase;
    
    private ProcessUploadUseCase processUploadUseCase;
    
    private ProcessUploadCommand validCommand;
    private UsuarioId usuarioId;
    
    @BeforeEach
    void setUp() {
        processUploadUseCase = new ProcessUploadUseCase(importExcelUseCase, generateErrorReportUseCase);
        
        usuarioId = new UsuarioId(1L);
        byte[] fileBytes = "conteudo do arquivo excel".getBytes();
        validCommand = new ProcessUploadCommand(fileBytes, "test.xlsx", usuarioId);
    }
    
    @Nested
    @DisplayName("Processamento com Sucesso")
    class ProcessamentoComSucesso {
        
        @Test
        @DisplayName("Deve processar upload sem erros")
        void deveProcessarUploadSemErros() {
            // Arrange
            ImportExcelResult importResult = ImportExcelResult.success(10);
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenReturn(importResult);
            
            // Act
            UploadProcessingResult result = processUploadUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.hasErrors());
            assertEquals(10, result.processedRows());
            assertEquals(10, result.successfulRows());
            assertTrue(result.errors().isEmpty());
            assertNull(result.errorReportStream());
            assertTrue(result.isCompleteSuccess());
            
            verify(importExcelUseCase).execute(argThat(cmd -> 
                cmd.usuarioId().equals(usuarioId)
            ));
            verify(generateErrorReportUseCase, never()).execute(any());
        }
        
        @Test
        @DisplayName("Deve calcular taxa de sucesso corretamente")
        void deveCalcularTaxaDeSucessoCorretamente() {
            // Arrange
            ImportExcelResult importResult = ImportExcelResult.success(25);
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenReturn(importResult);
            
            // Act
            UploadProcessingResult result = processUploadUseCase.execute(validCommand);
            
            // Assert
            assertEquals(100.0, result.getSuccessRate());
        }
    }
    
    @Nested
    @DisplayName("Processamento com Erros")
    class ProcessamentoComErros {
        
        @Test
        @DisplayName("Deve processar upload com erros e gerar relatório")
        void deveProcessarUploadComErrosEGerarRelatorio() {
            // Arrange
            List<ExcelRowError> errors = List.of(
                new ExcelRowError(2, "Erro na linha 2", Map.of("coluna1", "valor1")),
                new ExcelRowError(5, "Erro na linha 5", Map.of("coluna2", "valor2"))
            );
            
            ImportExcelResult importResult = ImportExcelResult.withErrors(errors, 10, 8);
            ByteArrayInputStream errorReport = new ByteArrayInputStream("relatorio de erros".getBytes());
            
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenReturn(importResult);
            when(generateErrorReportUseCase.execute(any(GenerateErrorReportCommand.class))).thenReturn(errorReport);
            
            // Act
            UploadProcessingResult result = processUploadUseCase.execute(validCommand);
            
            // Assert
            assertTrue(result.hasErrors());
            assertEquals(10, result.processedRows());
            assertEquals(8, result.successfulRows());
            assertEquals(2, result.getErrorRows());
            assertEquals(errors, result.errors());
            assertNotNull(result.errorReportStream());
            assertFalse(result.isCompleteSuccess());
            assertEquals(80.0, result.getSuccessRate());
            
            verify(importExcelUseCase).execute(any(ImportExcelCommand.class));
            verify(generateErrorReportUseCase).execute(argThat(cmd -> 
                cmd.errors().size() == 2
            ));
        }
        
        @Test
        @DisplayName("Deve tratar erro de processamento Excel")
        void deveTratarErroDeProcessamentoExcel() {
            // Arrange
            ExcelProcessingException exception = new ExcelProcessingException("Arquivo corrompido", new RuntimeException());
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenThrow(exception);
            
            // Act & Assert
            ExcelProcessingException thrown = assertThrows(
                ExcelProcessingException.class,
                () -> processUploadUseCase.execute(validCommand)
            );
            
            assertEquals("Arquivo corrompido", thrown.getMessage());
            verify(generateErrorReportUseCase, never()).execute(any());
        }
        
        @Test
        @DisplayName("Deve tratar erro inesperado")
        void deveTratarErroInesperado() {
            // Arrange
            RuntimeException exception = new RuntimeException("Erro inesperado");
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenThrow(exception);
            
            // Act & Assert
            ExcelProcessingException thrown = assertThrows(
                ExcelProcessingException.class,
                () -> processUploadUseCase.execute(validCommand)
            );
            
            assertTrue(thrown.getMessage().contains("Erro inesperado durante processamento"));
            assertEquals(exception, thrown.getCause());
        }
    }
    
    @Nested
    @DisplayName("Validação de Entrada")
    class ValidacaoDeEntrada {
        
        @Test
        @DisplayName("Deve rejeitar comando nulo")
        void deveRejeitarComandoNulo() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processUploadUseCase.execute(null)
            );
            
            assertEquals("Comando não pode ser nulo", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar arquivo vazio")
        void deveRejeitarArquivoVazio() {
            // Arrange
            ProcessUploadCommand commandArquivoVazio = new ProcessUploadCommand(
                new byte[0], "test.xlsx", usuarioId
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processUploadUseCase.execute(commandArquivoVazio)
            );
            
            assertEquals("Arquivo não pode estar vazio", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar nome de arquivo vazio")
        void deveRejeitarNomeDeArquivoVazio() {
            // Arrange
            ProcessUploadCommand commandNomeVazio = new ProcessUploadCommand(
                "conteudo".getBytes(), "", usuarioId
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processUploadUseCase.execute(commandNomeVazio)
            );
            
            assertEquals("Nome do arquivo é obrigatório", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar usuário nulo")
        void deveRejeitarUsuarioNulo() {
            // Arrange
            ProcessUploadCommand commandUsuarioNulo = new ProcessUploadCommand(
                "conteudo".getBytes(), "test.xlsx", null
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processUploadUseCase.execute(commandUsuarioNulo)
            );
            
            assertEquals("ID do usuário é obrigatório", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar arquivo muito grande")
        void deveRejeitarArquivoMuitoGrande() {
            // Arrange - Arquivo de 51MB
            byte[] arquivoGrande = new byte[51 * 1024 * 1024];
            ProcessUploadCommand commandArquivoGrande = new ProcessUploadCommand(
                arquivoGrande, "test.xlsx", usuarioId
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processUploadUseCase.execute(commandArquivoGrande)
            );
            
            assertEquals("Arquivo muito grande. Tamanho máximo: 50MB", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar extensão inválida")
        void deveRejeitarExtensaoInvalida() {
            // Arrange
            ProcessUploadCommand commandExtensaoInvalida = new ProcessUploadCommand(
                "conteudo".getBytes(), "test.xls", usuarioId
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processUploadUseCase.execute(commandExtensaoInvalida)
            );
            
            assertEquals("Apenas arquivos .xlsx são suportados", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve aceitar arquivo com tamanho limite")
        void deveAceitarArquivoComTamanhoLimite() {
            // Arrange - Arquivo de exatamente 50MB
            byte[] arquivoLimite = new byte[50 * 1024 * 1024];
            ProcessUploadCommand commandArquivoLimite = new ProcessUploadCommand(
                arquivoLimite, "test.xlsx", usuarioId
            );
            
            ImportExcelResult importResult = ImportExcelResult.success(1);
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenReturn(importResult);
            
            // Act & Assert - Não deve lançar exceção
            assertDoesNotThrow(() -> processUploadUseCase.execute(commandArquivoLimite));
        }
    }
    
    @Nested
    @DisplayName("Integração com Use Cases")
    class IntegracaoComUseCases {
        
        @Test
        @DisplayName("Deve passar parâmetros corretos para ImportExcelUseCase")
        void devePassarParametrosCorretosParaImportExcelUseCase() {
            // Arrange
            ImportExcelResult importResult = ImportExcelResult.success(5);
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenReturn(importResult);
            
            // Act
            processUploadUseCase.execute(validCommand);
            
            // Assert
            verify(importExcelUseCase).execute(argThat(cmd -> {
                return cmd.usuarioId().equals(usuarioId) &&
                       cmd.inputStream() != null;
            }));
        }
        
        @Test
        @DisplayName("Deve passar erros corretos para GenerateErrorReportUseCase")
        void devePassarErrosCorretosParaGenerateErrorReportUseCase() {
            // Arrange
            List<ExcelRowError> errors = List.of(
                new ExcelRowError(3, "Erro teste", Map.of("col", "val"))
            );
            
            ImportExcelResult importResult = ImportExcelResult.withErrors(errors, 5, 4);
            ByteArrayInputStream errorReport = new ByteArrayInputStream("report".getBytes());
            
            when(importExcelUseCase.execute(any(ImportExcelCommand.class))).thenReturn(importResult);
            when(generateErrorReportUseCase.execute(any(GenerateErrorReportCommand.class))).thenReturn(errorReport);
            
            // Act
            processUploadUseCase.execute(validCommand);
            
            // Assert
            verify(generateErrorReportUseCase).execute(argThat(cmd -> 
                cmd.errors().equals(errors)
            ));
        }
    }
}