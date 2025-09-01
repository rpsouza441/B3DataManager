package br.dev.rodrigopinheiro.B3DataManager.application.service.formatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para OperacaoFormatterService.
 * 
 * <p>Testa todas as regras de formatação de valores, preços e quantidades
 * seguindo os cenários de negócio da B3.</p>
 */
@DisplayName("OperacaoFormatterService")
class OperacaoFormatterServiceTest {
    
    private OperacaoFormatterService formatterService;
    
    @BeforeEach
    void setUp() {
        formatterService = new OperacaoFormatterService();
    }
    
    @Nested
    @DisplayName("Formatação de Valores")
    class FormatacaoValores {
        
        @Test
        @DisplayName("Deve retornar '-' para operações sem quantidade")
        void deveRetornarTracoParaOperacoesSemQuantidade() {
            // Arrange
            BigDecimal valor = new BigDecimal("100.50");
            BigDecimal quantidadeZero = BigDecimal.ZERO;
            
            // Act
            String resultado = formatterService.formatarValor(valor, quantidadeZero);
            
            // Assert
            assertEquals("-", resultado);
        }
        
        @Test
        @DisplayName("Deve retornar '-' para quantidade nula")
        void deveRetornarTracoParaQuantidadeNula() {
            // Arrange
            BigDecimal valor = new BigDecimal("100.50");
            BigDecimal quantidadeNula = null;
            
            // Act
            String resultado = formatterService.formatarValor(valor, quantidadeNula);
            
            // Assert
            assertEquals("-", resultado);
        }
        
        @Test
        @DisplayName("Deve retornar '-' para valor nulo")
        void deveRetornarTracoParaValorNulo() {
            // Arrange
            BigDecimal valorNulo = null;
            BigDecimal quantidade = new BigDecimal("10");
            
            // Act
            String resultado = formatterService.formatarValor(valorNulo, quantidade);
            
            // Assert
            assertEquals("-", resultado);
        }
        
        @Test
        @DisplayName("Deve retornar 'R$ 0,00' para operações gratuitas")
        void deveRetornarZeroParaOperacoesGratuitas() {
            // Arrange
            BigDecimal valorZero = BigDecimal.ZERO;
            BigDecimal quantidade = new BigDecimal("10");
            
            // Act
            String resultado = formatterService.formatarValor(valorZero, quantidade);
            
            // Assert
            assertEquals("R$ 0,00", resultado);
        }
        
        @Test
        @DisplayName("Deve formatar valores normais com 2 casas decimais")
        void deveFormatarValoresNormais() {
            // Arrange
            BigDecimal valor = new BigDecimal("1234.567");
            BigDecimal quantidade = new BigDecimal("100");
            
            // Act
            String resultado = formatterService.formatarValor(valor, quantidade);
            
            // Assert
            assertEquals("R$ 1234,57", resultado);
        }
        
        @Test
        @DisplayName("Deve formatar valores pequenos corretamente")
        void deveFormatarValoresPequenos() {
            // Arrange
            BigDecimal valor = new BigDecimal("0.96");
            BigDecimal quantidade = new BigDecimal("19");
            
            // Act
            String resultado = formatterService.formatarValor(valor, quantidade);
            
            // Assert
            assertEquals("R$ 0,96", resultado);
        }
    }
    
    @Nested
    @DisplayName("Formatação de Preços")
    class FormatacaoPrecos {
        
        @Test
        @DisplayName("Deve retornar '-' para operações sem quantidade")
        void deveRetornarTracoParaOperacoesSemQuantidade() {
            // Arrange
            BigDecimal preco = new BigDecimal("10.555");
            BigDecimal quantidadeZero = BigDecimal.ZERO;
            
            // Act
            String resultado = formatterService.formatarPreco(preco, quantidadeZero);
            
            // Assert
            assertEquals("-", resultado);
        }
        
        @Test
        @DisplayName("Deve retornar '-' para preço nulo")
        void deveRetornarTracoParaPrecoNulo() {
            // Arrange
            BigDecimal precoNulo = null;
            BigDecimal quantidade = new BigDecimal("10");
            
            // Act
            String resultado = formatterService.formatarPreco(precoNulo, quantidade);
            
            // Assert
            assertEquals("-", resultado);
        }
        
        @Test
        @DisplayName("Deve formatar preços com 3 casas decimais")
        void deveFormatarPrecosComTresCasasDecimais() {
            // Arrange
            BigDecimal preco = new BigDecimal("0.059");
            BigDecimal quantidade = new BigDecimal("19");
            
            // Act
            String resultado = formatterService.formatarPreco(preco, quantidade);
            
            // Assert
            assertEquals("R$ 0,059", resultado);
        }
        
        @Test
        @DisplayName("Deve formatar preços maiores corretamente")
        void deveFormatarPrecosMaiores() {
            // Arrange
            BigDecimal preco = new BigDecimal("1.942");
            BigDecimal quantidade = new BigDecimal("20");
            
            // Act
            String resultado = formatterService.formatarPreco(preco, quantidade);
            
            // Assert
            assertEquals("R$ 1,942", resultado);
        }
    }
    
    @Nested
    @DisplayName("Formatação de Quantidades")
    class FormatacaoQuantidades {
        
        @Test
        @DisplayName("Deve retornar '0' para quantidade nula")
        void deveRetornarZeroParaQuantidadeNula() {
            // Arrange
            BigDecimal quantidadeNula = null;
            
            // Act
            String resultado = formatterService.formatarQuantidade(quantidadeNula);
            
            // Assert
            assertEquals("0", resultado);
        }
        
        @Test
        @DisplayName("Deve retornar '0' para quantidade zero")
        void deveRetornarZeroParaQuantidadeZero() {
            // Arrange
            BigDecimal quantidadeZero = BigDecimal.ZERO;
            
            // Act
            String resultado = formatterService.formatarQuantidade(quantidadeZero);
            
            // Assert
            assertEquals("0", resultado);
        }
        
        @Test
        @DisplayName("Deve formatar quantidades inteiras")
        void deveFormatarQuantidadesInteiras() {
            // Arrange
            BigDecimal quantidade = new BigDecimal("100");
            
            // Act
            String resultado = formatterService.formatarQuantidade(quantidade);
            
            // Assert
            assertEquals("100", resultado);
        }
        
        @Test
        @DisplayName("Deve remover zeros desnecessários")
        void deveRemoverZerosDesnecessarios() {
            // Arrange
            BigDecimal quantidade = new BigDecimal("100.000");
            
            // Act
            String resultado = formatterService.formatarQuantidade(quantidade);
            
            // Assert
            assertEquals("100", resultado);
        }
        
        @Test
        @DisplayName("Deve manter decimais necessários")
        void deveManterDecimaisNecessarios() {
            // Arrange
            BigDecimal quantidade = new BigDecimal("100.50");
            
            // Act
            String resultado = formatterService.formatarQuantidade(quantidade);
            
            // Assert
            assertEquals("100.5", resultado);
        }
        
        @Test
        @DisplayName("Deve evitar notação científica")
        void deveEvitarNotacaoCientifica() {
            // Arrange
            BigDecimal quantidade = new BigDecimal("0.000001");
            
            // Act
            String resultado = formatterService.formatarQuantidade(quantidade);
            
            // Assert
            assertEquals("0.000001", resultado);
            assertFalse(resultado.contains("E"));
        }
    }
    
    @Nested
    @DisplayName("Formatação de Diferenças")
    class FormatacaoDiferencas {
        
        @Test
        @DisplayName("Deve retornar string vazia para diferença nula")
        void deveRetornarVazioParaDiferencaNula() {
            // Arrange
            BigDecimal diferencaNula = null;
            
            // Act
            String resultado = formatterService.formatarDiferenca(diferencaNula);
            
            // Assert
            assertEquals("", resultado);
        }
        
        @Test
        @DisplayName("Deve retornar string vazia para diferença zero")
        void deveRetornarVazioParaDiferencaZero() {
            // Arrange
            BigDecimal diferencaZero = BigDecimal.ZERO;
            
            // Act
            String resultado = formatterService.formatarDiferenca(diferencaZero);
            
            // Assert
            assertEquals("", resultado);
        }
        
        @Test
        @DisplayName("Deve formatar diferenças positivas")
        void deveFormatarDiferencasPositivas() {
            // Arrange
            BigDecimal diferenca = new BigDecimal("5.50");
            
            // Act
            String resultado = formatterService.formatarDiferenca(diferenca);
            
            // Assert
            assertEquals("R$ 5,50", resultado);
        }
        
        @Test
        @DisplayName("Deve formatar diferenças negativas")
        void deveFormatarDiferencasNegativas() {
            // Arrange
            BigDecimal diferenca = new BigDecimal("-2.75");
            
            // Act
            String resultado = formatterService.formatarDiferenca(diferenca);
            
            // Assert
            assertEquals("R$ -2,75", resultado);
        }
    }
    
    @Nested
    @DisplayName("Validação de Exibição")
    class ValidacaoExibicao {
        
        @Test
        @DisplayName("Deve indicar que não deve exibir valores para quantidade zero")
        void naoDeveExibirValoresParaQuantidadeZero() {
            // Arrange
            BigDecimal quantidadeZero = BigDecimal.ZERO;
            
            // Act
            boolean resultado = formatterService.deveExibirValores(quantidadeZero);
            
            // Assert
            assertFalse(resultado);
        }
        
        @Test
        @DisplayName("Deve indicar que não deve exibir valores para quantidade nula")
        void naoDeveExibirValoresParaQuantidadeNula() {
            // Arrange
            BigDecimal quantidadeNula = null;
            
            // Act
            boolean resultado = formatterService.deveExibirValores(quantidadeNula);
            
            // Assert
            assertFalse(resultado);
        }
        
        @Test
        @DisplayName("Deve indicar que deve exibir valores para quantidade positiva")
        void deveExibirValoresParaQuantidadePositiva() {
            // Arrange
            BigDecimal quantidadePositiva = new BigDecimal("10");
            
            // Act
            boolean resultado = formatterService.deveExibirValores(quantidadePositiva);
            
            // Assert
            assertTrue(resultado);
        }
    }
}