package br.dev.rodrigopinheiro.B3DataManager.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para apresentação de operações na camada de apresentação.
 * Contém apenas os dados necessários para exibição.
 */
public record OperacaoDTO(
    Long id,
    String entradaSaida,
    LocalDate data,
    String movimentacao,
    String produto,
    String instituicao,
    BigDecimal quantidade,
    BigDecimal precoUnitario,
    BigDecimal valorOperacao,     // Valor original da B3
    BigDecimal valorCalculado,   // Valor calculado (quantidade × preço)
    Boolean duplicado,
    Boolean dimensionado,
    Boolean temDiferencaValor,   // Indica se há diferença entre os valores
    BigDecimal diferencaValor    // Diferença absoluta entre os valores
) {}