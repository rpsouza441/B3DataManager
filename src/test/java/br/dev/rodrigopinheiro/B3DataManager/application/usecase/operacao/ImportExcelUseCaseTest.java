package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CheckDuplicateCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ImportExcelCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.RegisterOperacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.CheckDuplicateResult;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ImportExcelResult;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.CheckDuplicateOperacaoUseCase;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.RegisterOperacaoUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.ExcelProcessingException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.InvalidDataException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ImportExcelUseCase.
 * 
 * <p>Testa todos os cenários de importação de Excel,
 * incluindo sucessos, erros, validações e duplicatas.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ImportExcelUseCase")
class ImportExcelUseCaseTest {
    
    @Mock
    private RegisterOperacaoUseCase registerOperacaoUseCase;
    
    @Mock
    private CheckDuplicateOperacaoUseCase checkDuplicateUseCase;
    
    private ImportExcelUseCase importExcelUseCase;
    
    private UsuarioId usuarioId;
    private ImportExcelCommand validCommand;
    
    @BeforeEach
    void setUp() {
        importExcelUseCase = new ImportExcelUseCase(registerOperacaoUseCase, checkDuplicateUseCase);
        usuarioId = new UsuarioId(1L);
    }
    
    @Nested
    @DisplayName("Importação com Sucesso")
    class ImportacaoComSucesso {
        
        @Test
        @DisplayName("Deve importar arquivo Excel válido sem erros")
        void deveImportarArquivoExcelValidoSemErros() throws IOException {
            // Arrange
            InputStream excelStream = createValidExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            

            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.hasErrors());
            assertEquals(2, result.processedRows());
            assertEquals(2, result.successfulRows());
            assertTrue(result.errors().isEmpty());
            
            verify(registerOperacaoUseCase, times(2)).execute(any(RegisterOperacaoCommand.class));
            verify(checkDuplicateUseCase, times(2)).execute(any(CheckDuplicateCommand.class));
        }
        
        @Test
        @DisplayName("Deve processar arquivo com uma linha válida")
        void deveProcessarArquivoComUmaLinhaValida() throws IOException {
            // Arrange
            InputStream excelStream = createSingleRowExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            

            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.hasErrors());
            assertEquals(1, result.processedRows());
            assertEquals(1, result.successfulRows());
            
            verify(registerOperacaoUseCase, times(1)).execute(any(RegisterOperacaoCommand.class));
        }
    }
    
    @Nested
    @DisplayName("Importação com Erros")
    class ImportacaoComErros {
        
        @Test
        @DisplayName("Deve coletar erros de dados inválidos")
        void deveColetarErrosDeDadosInvalidos() throws IOException {
            // Arrange
            InputStream excelStream = createInvalidDataExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertTrue(result.hasErrors());
            assertEquals(1, result.processedRows());
            assertEquals(0, result.successfulRows());
            assertEquals(1, result.errors().size());
            
            ExcelRowError error = result.errors().get(0);
            assertEquals(2, error.rowNumber()); // Linha 2 (primeira linha de dados)
            assertTrue(error.errorMessage().contains("Valor numérico inválido"));
            assertNotNull(error.originalData());
            
            verify(registerOperacaoUseCase, never()).execute(any(RegisterOperacaoCommand.class));
        }
        
        @Test
        @DisplayName("Deve coletar erros de operação inválida")
        void deveColetarErrosDeOperacaoInvalida() throws IOException {
            // Arrange
            InputStream excelStream = createValidExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            

            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            when(registerOperacaoUseCase.execute(any(RegisterOperacaoCommand.class)))
                .thenThrow(new OperacaoInvalidaException("Operação inválida"));
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertTrue(result.hasErrors());
            assertEquals(2, result.processedRows());
            assertEquals(0, result.successfulRows());
            assertEquals(2, result.errors().size());
            
            result.errors().forEach(error -> {
                assertTrue(error.errorMessage().contains("Operação inválida"));
                assertNotNull(error.originalData());
            });
        }
        
        @Test
        @DisplayName("Deve processar arquivo misto (sucessos e erros)")
        void deveProcessarArquivoMisto() throws IOException {
            // Arrange
            InputStream excelStream = createMixedDataExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            
            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertTrue(result.hasErrors());
            assertEquals(2, result.processedRows());
            assertEquals(1, result.successfulRows());
            assertEquals(1, result.errors().size());
            
            verify(registerOperacaoUseCase, times(1)).execute(any(RegisterOperacaoCommand.class));
        }
    }
    
    @Nested
    @DisplayName("Detecção de Duplicatas")
    class DeteccaoDeDuplicatas {
        
        @Test
        @DisplayName("Deve detectar e marcar operações duplicadas")
        void deveDetectarEMarcarOperacoesDuplicadas() throws IOException {
            // Arrange
            InputStream excelStream = createValidExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            

            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.duplicate(123L)); // Primeira é duplicata
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertTrue(result.hasErrors());
            assertEquals(2, result.processedRows());
            assertEquals(0, result.successfulRows());
            assertEquals(2, result.errors().size());
            
            result.errors().forEach(error -> {
                assertTrue(error.errorMessage().contains("Operação duplicada"));
                assertTrue(error.errorMessage().contains("123"));
            });
            
            verify(registerOperacaoUseCase, never()).execute(any(RegisterOperacaoCommand.class));
        }
        
        @Test
        @DisplayName("Deve processar operações não duplicadas normalmente")
        void deveProcessarOperacoesNaoDuplicadasNormalmente() throws IOException {
            // Arrange
            InputStream excelStream = createValidExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            

            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.hasErrors());
            assertEquals(2, result.successfulRows());
            
            verify(checkDuplicateUseCase, times(2)).execute(any(CheckDuplicateCommand.class));
            verify(registerOperacaoUseCase, times(2)).execute(any(RegisterOperacaoCommand.class));
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
                () -> importExcelUseCase.execute(null)
            );
            
            assertEquals("Comando não pode ser nulo", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar stream nulo")
        void deveRejeitarStreamNulo() {
            // Arrange
            ImportExcelCommand commandStreamNulo = new ImportExcelCommand(null, usuarioId);
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importExcelUseCase.execute(commandStreamNulo)
            );
            
            assertEquals("InputStream não pode ser nulo", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar usuário nulo")
        void deveRejeitarUsuarioNulo() throws IOException {
            // Arrange
            InputStream excelStream = createValidExcelStream();
            ImportExcelCommand commandUsuarioNulo = new ImportExcelCommand(excelStream, null);
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importExcelUseCase.execute(commandUsuarioNulo)
            );
            
            assertEquals("Usuário é obrigatório", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Tratamento de Erros")
    class TratamentoDeErros {
        
        @Test
        @DisplayName("Deve tratar erro de leitura do Excel")
        void deveTratarErroDeleituraDoExcel() {
            // Arrange
            InputStream invalidStream = new ByteArrayInputStream("conteudo invalido".getBytes());
            validCommand = new ImportExcelCommand(invalidStream, usuarioId);
            
            // Act & Assert
            ExcelProcessingException exception = assertThrows(
                ExcelProcessingException.class,
                () -> importExcelUseCase.execute(validCommand)
            );
            
            assertTrue(exception.getMessage().contains("Erro ao processar arquivo Excel"));
        }
        
        @Test
        @DisplayName("Deve tratar arquivo Excel vazio")
        void deveTratarArquivoExcelVazio() throws IOException {
            // Arrange
            InputStream emptyExcelStream = createEmptyExcelStream();
            validCommand = new ImportExcelCommand(emptyExcelStream, usuarioId);
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.hasErrors());
            assertEquals(0, result.processedRows());
            assertEquals(0, result.successfulRows());
            assertTrue(result.errors().isEmpty());
        }
        
        @Test
        @DisplayName("Deve tratar erro inesperado durante processamento")
        void deveTratarErroInesperadoDuranteProcessamento() throws IOException {
            // Arrange
            InputStream excelStream = createValidExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            
            // Simular erro no RegisterOperacaoUseCase
            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            when(registerOperacaoUseCase.execute(any(RegisterOperacaoCommand.class)))
                .thenThrow(new RuntimeException("Erro inesperado"));
            
            // Act & Assert
            ExcelProcessingException exception = assertThrows(
                ExcelProcessingException.class,
                () -> importExcelUseCase.execute(validCommand)
            );
            
            assertTrue(exception.getMessage().contains("Erro inesperado durante importação"));
        }
    }
    
    @Nested
    @DisplayName("Parsing de Valores")
    class ParsingDeValores {
        
        @Test
        @DisplayName("Deve processar valores monetários brasileiros")
        void deveProcessarValoresMonetariosBrasileiros() throws IOException {
            // Arrange
            InputStream excelStream = createBrazilianValuesExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            

            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.hasErrors());
            assertEquals(1, result.successfulRows());
        }
        
        @Test
        @DisplayName("Deve tratar hífens como zero")
        void deveTratarHifensComoZero() throws IOException {
            // Arrange
            InputStream excelStream = createHyphenValuesExcelStream();
            validCommand = new ImportExcelCommand(excelStream, usuarioId);
            

            when(checkDuplicateUseCase.execute(any(CheckDuplicateCommand.class)))
                .thenReturn(CheckDuplicateResult.notDuplicate());
            
            // Act
            ImportExcelResult result = importExcelUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.hasErrors());
            assertEquals(1, result.successfulRows());
        }
    }
    
    // Métodos auxiliares para criar streams Excel de teste
    
    private InputStream createValidExcelStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Operações");
        
        // Cabeçalho
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Entrada/Saída");
        headerRow.createCell(1).setCellValue("Data");
        headerRow.createCell(2).setCellValue("Movimentação");
        headerRow.createCell(3).setCellValue("Produto");
        headerRow.createCell(4).setCellValue("Instituição");
        headerRow.createCell(5).setCellValue("Quantidade");
        headerRow.createCell(6).setCellValue("Preço unitário");
        headerRow.createCell(7).setCellValue("Valor da Operação");
        
        // Dados válidos
        Row dataRow1 = sheet.createRow(1);
        dataRow1.createCell(0).setCellValue("Entrada");
        dataRow1.createCell(1).setCellValue("29/08/2025");
        dataRow1.createCell(2).setCellValue("Juros Sobre Capital Próprio");
        dataRow1.createCell(3).setCellValue("ITSA4 - ITAUSA S.A.");
        dataRow1.createCell(4).setCellValue("INTER DISTRIBUIDORA");
        dataRow1.createCell(5).setCellValue("19");
        dataRow1.createCell(6).setCellValue("R$0,059");
        dataRow1.createCell(7).setCellValue("R$0,96");
        
        Row dataRow2 = sheet.createRow(2);
        dataRow2.createCell(0).setCellValue("Saída");
        dataRow2.createCell(1).setCellValue("30/08/2025");
        dataRow2.createCell(2).setCellValue("Venda");
        dataRow2.createCell(3).setCellValue("PETR4 - PETROBRAS S.A.");
        dataRow2.createCell(4).setCellValue("XP INVESTIMENTOS");
        dataRow2.createCell(5).setCellValue("100");
        dataRow2.createCell(6).setCellValue("35,50");
        dataRow2.createCell(7).setCellValue("3550,00");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    
    private InputStream createSingleRowExcelStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Operações");
        
        // Cabeçalho
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Entrada/Saída");
        headerRow.createCell(1).setCellValue("Data");
        headerRow.createCell(2).setCellValue("Movimentação");
        headerRow.createCell(3).setCellValue("Produto");
        headerRow.createCell(4).setCellValue("Instituição");
        headerRow.createCell(5).setCellValue("Quantidade");
        headerRow.createCell(6).setCellValue("Preço unitário");
        headerRow.createCell(7).setCellValue("Valor da Operação");
        
        // Uma linha de dados
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Entrada");
        dataRow.createCell(1).setCellValue("29/08/2025");
        dataRow.createCell(2).setCellValue("Dividendo");
        dataRow.createCell(3).setCellValue("ITSA4 - ITAUSA S.A.");
        dataRow.createCell(4).setCellValue("INTER DISTRIBUIDORA");
        dataRow.createCell(5).setCellValue("10");
        dataRow.createCell(6).setCellValue("1,50");
        dataRow.createCell(7).setCellValue("15,00");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    
    private InputStream createInvalidDataExcelStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Operações");
        
        // Cabeçalho
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Entrada/Saída");
        headerRow.createCell(1).setCellValue("Data");
        headerRow.createCell(2).setCellValue("Movimentação");
        headerRow.createCell(3).setCellValue("Produto");
        headerRow.createCell(4).setCellValue("Instituição");
        headerRow.createCell(5).setCellValue("Quantidade");
        headerRow.createCell(6).setCellValue("Preço unitário");
        headerRow.createCell(7).setCellValue("Valor da Operação");
        
        // Dados inválidos
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Entrada");
        dataRow.createCell(1).setCellValue("29/08/2025");
        dataRow.createCell(2).setCellValue("Juros");
        dataRow.createCell(3).setCellValue("ITSA4");
        dataRow.createCell(4).setCellValue("INTER");
        dataRow.createCell(5).setCellValue("19");
        dataRow.createCell(6).setCellValue("R$0,059");
        dataRow.createCell(7).setCellValue("abc"); // Valor inválido
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    
    private InputStream createMixedDataExcelStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Operações");
        
        // Cabeçalho
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Entrada/Saída");
        headerRow.createCell(1).setCellValue("Data");
        headerRow.createCell(2).setCellValue("Movimentação");
        headerRow.createCell(3).setCellValue("Produto");
        headerRow.createCell(4).setCellValue("Instituição");
        headerRow.createCell(5).setCellValue("Quantidade");
        headerRow.createCell(6).setCellValue("Preço unitário");
        headerRow.createCell(7).setCellValue("Valor da Operação");
        
        // Linha válida
        Row validRow = sheet.createRow(1);
        validRow.createCell(0).setCellValue("Entrada");
        validRow.createCell(1).setCellValue("29/08/2025");
        validRow.createCell(2).setCellValue("Dividendo");
        validRow.createCell(3).setCellValue("ITSA4");
        validRow.createCell(4).setCellValue("INTER");
        validRow.createCell(5).setCellValue("10");
        validRow.createCell(6).setCellValue("1,50");
        validRow.createCell(7).setCellValue("15,00");
        
        // Linha inválida
        Row invalidRow = sheet.createRow(2);
        invalidRow.createCell(0).setCellValue("Saída");
        invalidRow.createCell(1).setCellValue("30/08/2025");
        invalidRow.createCell(2).setCellValue("Venda");
        invalidRow.createCell(3).setCellValue("PETR4");
        invalidRow.createCell(4).setCellValue("XP");
        invalidRow.createCell(5).setCellValue("xyz"); // Quantidade inválida
        invalidRow.createCell(6).setCellValue("35,50");
        invalidRow.createCell(7).setCellValue("3550,00");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    
    private InputStream createEmptyExcelStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Operações");
        
        // Apenas cabeçalho, sem dados
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Entrada/Saída");
        headerRow.createCell(1).setCellValue("Data");
        headerRow.createCell(2).setCellValue("Movimentação");
        headerRow.createCell(3).setCellValue("Produto");
        headerRow.createCell(4).setCellValue("Instituição");
        headerRow.createCell(5).setCellValue("Quantidade");
        headerRow.createCell(6).setCellValue("Preço unitário");
        headerRow.createCell(7).setCellValue("Valor da Operação");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    
    private InputStream createBrazilianValuesExcelStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Operações");
        
        // Cabeçalho
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Entrada/Saída");
        headerRow.createCell(1).setCellValue("Data");
        headerRow.createCell(2).setCellValue("Movimentação");
        headerRow.createCell(3).setCellValue("Produto");
        headerRow.createCell(4).setCellValue("Instituição");
        headerRow.createCell(5).setCellValue("Quantidade");
        headerRow.createCell(6).setCellValue("Preço unitário");
        headerRow.createCell(7).setCellValue("Valor da Operação");
        
        // Dados com valores brasileiros
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Entrada");
        dataRow.createCell(1).setCellValue("29/08/2025");
        dataRow.createCell(2).setCellValue("Juros");
        dataRow.createCell(3).setCellValue("ITSA4");
        dataRow.createCell(4).setCellValue("INTER");
        dataRow.createCell(5).setCellValue("19");
        dataRow.createCell(6).setCellValue("R$ 0,059"); // Com espaço e R$
        dataRow.createCell(7).setCellValue("R$ 1.234,56"); // Formato brasileiro completo
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    
    private InputStream createHyphenValuesExcelStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Operações");
        
        // Cabeçalho
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Entrada/Saída");
        headerRow.createCell(1).setCellValue("Data");
        headerRow.createCell(2).setCellValue("Movimentação");
        headerRow.createCell(3).setCellValue("Produto");
        headerRow.createCell(4).setCellValue("Instituição");
        headerRow.createCell(5).setCellValue("Quantidade");
        headerRow.createCell(6).setCellValue("Preço unitário");
        headerRow.createCell(7).setCellValue("Valor da Operação");
        
        // Dados com hífens (direitos não exercidos)
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Entrada");
        dataRow.createCell(1).setCellValue("29/08/2025");
        dataRow.createCell(2).setCellValue("Direito de Subscrição - Não Exercido");
        dataRow.createCell(3).setCellValue("ITSA4");
        dataRow.createCell(4).setCellValue("INTER");
        dataRow.createCell(5).setCellValue("0");
        dataRow.createCell(6).setCellValue("-"); // Hífen para preço
        dataRow.createCell(7).setCellValue("-"); // Hífen para valor
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}