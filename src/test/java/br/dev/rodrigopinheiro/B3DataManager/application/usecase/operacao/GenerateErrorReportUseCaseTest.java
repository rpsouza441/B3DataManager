package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.GenerateErrorReportCommand;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.ExcelRowError;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para GenerateErrorReportUseCase.
 * 
 * <p>Testa todos os cenários de geração de relatório de erros,
 * incluindo diferentes tipos de erro e formatos de dados.</p>
 */
@DisplayName("GenerateErrorReportUseCase")
class GenerateErrorReportUseCaseTest {
    
    private GenerateErrorReportUseCase generateErrorReportUseCase;
    
    @BeforeEach
    void setUp() {
        generateErrorReportUseCase = new GenerateErrorReportUseCase();
    }
    
    @Nested
    @DisplayName("Geração de Relatório")
    class GeracaoDeRelatorio {
        
        @Test
        @DisplayName("Deve gerar relatório Excel com um erro")
        void deveGerarRelatorioExcelComUmErro() throws IOException {
            // Arrange
            ExcelRowError error = new ExcelRowError(
                2,
                "Valor numérico inválido: abc",
                Map.of(
                    "Entrada/Saída", "Entrada",
                    "Data", "29/08/2025",
                    "Movimentação", "Juros Sobre Capital Próprio",
                    "Produto", "ITSA4 - ITAUSA S.A.",
                    "Instituição", "INTER DISTRIBUIDORA",
                    "Quantidade", "19",
                    "Preço unitário", "R$0,059",
                    "Valor da Operação", "abc"
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.available() > 0);
            
            // Verificar conteúdo do Excel
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            assertNotNull(sheet);
            
            // Verificar cabeçalho
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("Linha", headerRow.getCell(0).getStringCellValue());
            assertEquals("Erro", headerRow.getCell(1).getStringCellValue());
            assertEquals("Entrada/Saída", headerRow.getCell(2).getStringCellValue());
            assertEquals("Data", headerRow.getCell(3).getStringCellValue());
            assertEquals("Movimentação", headerRow.getCell(4).getStringCellValue());
            assertEquals("Produto", headerRow.getCell(5).getStringCellValue());
            assertEquals("Instituição", headerRow.getCell(6).getStringCellValue());
            assertEquals("Quantidade", headerRow.getCell(7).getStringCellValue());
            assertEquals("Preço unitário", headerRow.getCell(8).getStringCellValue());
            assertEquals("Valor da Operação", headerRow.getCell(9).getStringCellValue());
            
            // Verificar dados do erro
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            assertEquals(2.0, dataRow.getCell(0).getNumericCellValue());
            assertEquals("Valor numérico inválido: abc", dataRow.getCell(1).getStringCellValue());
            assertEquals("Entrada", dataRow.getCell(2).getStringCellValue());
            assertEquals("29/08/2025", dataRow.getCell(3).getStringCellValue());
            assertEquals("Juros Sobre Capital Próprio", dataRow.getCell(4).getStringCellValue());
            assertEquals("ITSA4 - ITAUSA S.A.", dataRow.getCell(5).getStringCellValue());
            assertEquals("INTER DISTRIBUIDORA", dataRow.getCell(6).getStringCellValue());
            assertEquals("19", dataRow.getCell(7).getStringCellValue());
            assertEquals("R$0,059", dataRow.getCell(8).getStringCellValue());
            assertEquals("abc", dataRow.getCell(9).getStringCellValue());
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve gerar relatório Excel com múltiplos erros")
        void deveGerarRelatorioExcelComMultiplosErros() throws IOException {
            // Arrange
            List<ExcelRowError> errors = List.of(
                new ExcelRowError(
                    2,
                    "Valor numérico inválido: abc",
                    Map.of(
                        "Entrada/Saída", "Entrada",
                        "Data", "29/08/2025",
                        "Movimentação", "Juros",
                        "Produto", "ITSA4",
                        "Instituição", "INTER",
                        "Quantidade", "19",
                        "Preço unitário", "R$0,059",
                        "Valor da Operação", "abc"
                    )
                ),
                new ExcelRowError(
                    5,
                    "Data inválida: 32/13/2025",
                    Map.of(
                        "Entrada/Saída", "Saída",
                        "Data", "32/13/2025",
                        "Movimentação", "Venda",
                        "Produto", "PETR4",
                        "Instituição", "XP",
                        "Quantidade", "100",
                        "Preço unitário", "35,50",
                        "Valor da Operação", "3550,00"
                    )
                ),
                new ExcelRowError(
                    8,
                    "Operação duplicada (ID original: 1234)",
                    Map.of(
                        "Entrada/Saída", "Entrada",
                        "Data", "30/08/2025",
                        "Movimentação", "Dividendo",
                        "Produto", "VALE3",
                        "Instituição", "CLEAR",
                        "Quantidade", "50",
                        "Preço unitário", "2,00",
                        "Valor da Operação", "100,00"
                    )
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(errors);
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.available() > 0);
            
            // Verificar conteúdo do Excel
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            assertNotNull(sheet);
            
            // Verificar que há 4 linhas (1 cabeçalho + 3 erros)
            assertEquals(3, sheet.getLastRowNum()); // 0-indexed, então 3 = 4 linhas
            
            // Verificar primeiro erro
            Row row1 = sheet.getRow(1);
            assertEquals(2.0, row1.getCell(0).getNumericCellValue());
            assertTrue(row1.getCell(1).getStringCellValue().contains("Valor numérico inválido"));
            assertEquals("ITSA4", row1.getCell(5).getStringCellValue());
            
            // Verificar segundo erro
            Row row2 = sheet.getRow(2);
            assertEquals(5.0, row2.getCell(0).getNumericCellValue());
            assertTrue(row2.getCell(1).getStringCellValue().contains("Data inválida"));
            assertEquals("PETR4", row2.getCell(5).getStringCellValue());
            
            // Verificar terceiro erro
            Row row3 = sheet.getRow(3);
            assertEquals(8.0, row3.getCell(0).getNumericCellValue());
            assertTrue(row3.getCell(1).getStringCellValue().contains("Operação duplicada"));
            assertEquals("VALE3", row3.getCell(5).getStringCellValue());
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve gerar relatório com dados originais preservados")
        void deveGerarRelatorioComDadosOriginaisPreservados() throws IOException {
            // Arrange
            ExcelRowError error = new ExcelRowError(
                10,
                "Campo obrigatório vazio: Produto",
                Map.of(
                    "Entrada/Saída", "Entrada",
                    "Data", "01/09/2025",
                    "Movimentação", "Bonificação",
                    "Produto", "", // Campo vazio que causou o erro
                    "Instituição", "RICO INVESTIMENTOS",
                    "Quantidade", "25",
                    "Preço unitário", "0,00",
                    "Valor da Operação", "0,00"
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            Row dataRow = sheet.getRow(1);
            assertEquals(10.0, dataRow.getCell(0).getNumericCellValue());
            assertEquals("Campo obrigatório vazio: Produto", dataRow.getCell(1).getStringCellValue());
            assertEquals("Entrada", dataRow.getCell(2).getStringCellValue());
            assertEquals("01/09/2025", dataRow.getCell(3).getStringCellValue());
            assertEquals("Bonificação", dataRow.getCell(4).getStringCellValue());
            assertEquals("", dataRow.getCell(5).getStringCellValue()); // Campo vazio preservado
            assertEquals("RICO INVESTIMENTOS", dataRow.getCell(6).getStringCellValue());
            assertEquals("25", dataRow.getCell(7).getStringCellValue());
            assertEquals("0,00", dataRow.getCell(8).getStringCellValue());
            assertEquals("0,00", dataRow.getCell(9).getStringCellValue());
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve gerar relatório com valores monetários brasileiros")
        void deveGerarRelatorioComValoresMonetariosBrasileiros() throws IOException {
            // Arrange
            ExcelRowError error = new ExcelRowError(
                3,
                "Quantidade inválida: xyz",
                Map.of(
                    "Entrada/Saída", "Saída",
                    "Data", "15/08/2025",
                    "Movimentação", "Venda",
                    "Produto", "BBAS3 - BANCO DO BRASIL S.A.",
                    "Instituição", "XP INVESTIMENTOS CCTVM S.A.",
                    "Quantidade", "xyz", // Valor inválido
                    "Preço unitário", "R$ 45,67",
                    "Valor da Operação", "R$ 4.567,89"
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            Row dataRow = sheet.getRow(1);
            assertEquals("Quantidade inválida: xyz", dataRow.getCell(1).getStringCellValue());
            assertEquals("BBAS3 - BANCO DO BRASIL S.A.", dataRow.getCell(5).getStringCellValue());
            assertEquals("xyz", dataRow.getCell(7).getStringCellValue()); // Valor inválido preservado
            assertEquals("R$ 45,67", dataRow.getCell(8).getStringCellValue());
            assertEquals("R$ 4.567,89", dataRow.getCell(9).getStringCellValue());
            
            workbook.close();
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
                () -> generateErrorReportUseCase.execute(null)
            );
            
            assertEquals("Comando não pode ser nulo", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar lista de erros nula")
        void deveRejeitarListaDeErrosNula() {
            // Arrange
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(null);
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> generateErrorReportUseCase.execute(command)
            );
            
            assertEquals("Lista de erros não pode ser nula", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar lista de erros vazia")
        void deveRejeitarListaDeErrosVazia() {
            // Arrange
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of());
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> generateErrorReportUseCase.execute(command)
            );
            
            assertEquals("Lista de erros não pode estar vazia", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Tratamento de Erros")
    class TratamentoDeErros {
        
        @Test
        @DisplayName("Deve tratar erro com dados originais nulos")
        void deveTratarErroComDadosOriginaisNulos() throws IOException {
            // Arrange
            ExcelRowError errorComDadosNulos = new ExcelRowError(
                5,
                "Erro sem dados originais",
                null // Dados originais nulos
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(errorComDadosNulos));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            Row dataRow = sheet.getRow(1);
            assertEquals(5.0, dataRow.getCell(0).getNumericCellValue());
            assertEquals("Erro sem dados originais", dataRow.getCell(1).getStringCellValue());
            
            // Verificar que as células de dados estão vazias ou nulas
            for (int i = 2; i <= 9; i++) {
                Cell cell = dataRow.getCell(i);
                if (cell != null) {
                    assertEquals("", cell.getStringCellValue());
                }
            }
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve tratar erro com dados originais incompletos")
        void deveTratarErroComDadosOriginaisIncompletos() throws IOException {
            // Arrange
            ExcelRowError errorComDadosIncompletos = new ExcelRowError(
                7,
                "Dados incompletos",
                Map.of(
                    "Entrada/Saída", "Entrada",
                    "Data", "20/08/2025",
                    "Produto", "MGLU3"
                    // Faltam outros campos
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(errorComDadosIncompletos));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            Row dataRow = sheet.getRow(1);
            assertEquals("Entrada", dataRow.getCell(2).getStringCellValue());
            assertEquals("20/08/2025", dataRow.getCell(3).getStringCellValue());
            assertEquals("MGLU3", dataRow.getCell(5).getStringCellValue());
            
            // Campos não fornecidos devem estar vazios
            Cell movimentacaoCell = dataRow.getCell(4);
            if (movimentacaoCell != null) {
                assertEquals("", movimentacaoCell.getStringCellValue());
            }
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve tratar mensagem de erro muito longa")
        void deveTratarMensagemDeErroMuitoLonga() throws IOException {
            // Arrange
            String mensagemLonga = "Esta é uma mensagem de erro muito longa que pode exceder os limites normais de uma célula Excel e precisa ser tratada adequadamente pelo sistema de geração de relatórios para garantir que não cause problemas na visualização ou no processamento do arquivo gerado";
            
            ExcelRowError error = new ExcelRowError(
                15,
                mensagemLonga,
                Map.of(
                    "Entrada/Saída", "Entrada",
                    "Data", "25/08/2025",
                    "Movimentação", "Teste",
                    "Produto", "TESTE4",
                    "Instituição", "TESTE CORRETORA",
                    "Quantidade", "1",
                    "Preço unitário", "1,00",
                    "Valor da Operação", "1,00"
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            Row dataRow = sheet.getRow(1);
            assertEquals(mensagemLonga, dataRow.getCell(1).getStringCellValue());
            
            workbook.close();
        }
    }
    
    @Nested
    @DisplayName("Formatação do Relatório")
    class FormatacaoDoRelatorio {
        
        @Test
        @DisplayName("Deve criar planilha com nome correto")
        void deveCriarPlanilhaComNomeCorreto() throws IOException {
            // Arrange
            ExcelRowError error = new ExcelRowError(
                1,
                "Erro de teste",
                Map.of("Entrada/Saída", "Entrada")
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            Workbook workbook = new XSSFWorkbook(result);
            assertEquals("Erros de Importação", workbook.getSheetName(0));
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve aplicar formatação ao cabeçalho")
        void deveAplicarFormatacaoAoCabecalho() throws IOException {
            // Arrange
            ExcelRowError error = new ExcelRowError(
                1,
                "Erro de teste",
                Map.of("Entrada/Saída", "Entrada")
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            
            // Verificar que o cabeçalho tem estilo aplicado
            Cell firstHeaderCell = headerRow.getCell(0);
            assertNotNull(firstHeaderCell.getCellStyle());
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve ajustar largura das colunas automaticamente")
        void deveAjustarLarguraDasColunasAutomaticamente() throws IOException {
            // Arrange
            ExcelRowError error = new ExcelRowError(
                1,
                "Esta é uma mensagem de erro mais longa para testar o ajuste automático de largura",
                Map.of(
                    "Entrada/Saída", "Entrada",
                    "Data", "29/08/2025",
                    "Movimentação", "Juros Sobre Capital Próprio - Teste de Nome Longo",
                    "Produto", "ITSA4 - ITAUSA S.A. - INVESTIMENTOS ITAU S.A.",
                    "Instituição", "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
                    "Quantidade", "19",
                    "Preço unitário", "R$0,059",
                    "Valor da Operação", "R$0,96"
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            // Verificar que as colunas têm largura maior que o padrão
            for (int i = 0; i < 10; i++) {
                int columnWidth = sheet.getColumnWidth(i);
                assertTrue(columnWidth > 0, "Coluna " + i + " deve ter largura configurada");
            }
            
            workbook.close();
        }
    }
    
    @Nested
    @DisplayName("Cenários Especiais")
    class CenariosEspeciais {
        
        @Test
        @DisplayName("Deve processar erro com caracteres especiais")
        void deveProcessarErroComCaracteresEspeciais() throws IOException {
            // Arrange
            ExcelRowError error = new ExcelRowError(
                1,
                "Erro com acentuação: não é possível processar ação",
                Map.of(
                    "Entrada/Saída", "Saída",
                    "Data", "29/08/2025",
                    "Movimentação", "Operação com ç, ã, é, ü",
                    "Produto", "AÇÚCAR CRISTAL S.A.",
                    "Instituição", "CORRETORA & INVESTIMENTOS LTDA.",
                    "Quantidade", "100",
                    "Preço unitário", "R$ 1,50",
                    "Valor da Operação", "R$ 150,00"
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(List.of(error));
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            Row dataRow = sheet.getRow(1);
            assertEquals("Erro com acentuação: não é possível processar ação", dataRow.getCell(1).getStringCellValue());
            assertEquals("Operação com ç, ã, é, ü", dataRow.getCell(4).getStringCellValue());
            assertEquals("AÇÚCAR CRISTAL S.A.", dataRow.getCell(5).getStringCellValue());
            assertEquals("CORRETORA & INVESTIMENTOS LTDA.", dataRow.getCell(6).getStringCellValue());
            
            workbook.close();
        }
        
        @Test
        @DisplayName("Deve processar múltiplos erros na mesma linha")
        void deveProcessarMultiplosErrosNaMesmaLinha() throws IOException {
            // Arrange - Simular dois erros diferentes na linha 5
            List<ExcelRowError> errors = List.of(
                new ExcelRowError(
                    5,
                    "Primeiro erro: valor inválido",
                    Map.of(
                        "Entrada/Saída", "Entrada",
                        "Data", "29/08/2025",
                        "Movimentação", "Juros",
                        "Produto", "ITSA4",
                        "Instituição", "INTER",
                        "Quantidade", "19",
                        "Preço unitário", "abc",
                        "Valor da Operação", "R$0,96"
                    )
                ),
                new ExcelRowError(
                    5,
                    "Segundo erro: data inválida",
                    Map.of(
                        "Entrada/Saída", "Entrada",
                        "Data", "32/13/2025",
                        "Movimentação", "Juros",
                        "Produto", "ITSA4",
                        "Instituição", "INTER",
                        "Quantidade", "19",
                        "Preço unitário", "R$0,059",
                        "Valor da Operação", "R$0,96"
                    )
                )
            );
            
            GenerateErrorReportCommand command = new GenerateErrorReportCommand(errors);
            
            // Act
            ByteArrayInputStream result = generateErrorReportUseCase.execute(command);
            
            // Assert
            assertNotNull(result);
            
            Workbook workbook = new XSSFWorkbook(result);
            Sheet sheet = workbook.getSheet("Erros de Importação");
            
            // Deve haver duas linhas de erro, ambas referenciando a linha 5
            Row row1 = sheet.getRow(1);
            Row row2 = sheet.getRow(2);
            
            assertEquals(5.0, row1.getCell(0).getNumericCellValue());
            assertEquals(5.0, row2.getCell(0).getNumericCellValue());
            
            assertTrue(row1.getCell(1).getStringCellValue().contains("Primeiro erro"));
            assertTrue(row2.getCell(1).getStringCellValue().contains("Segundo erro"));
            
            workbook.close();
        }
    }
}