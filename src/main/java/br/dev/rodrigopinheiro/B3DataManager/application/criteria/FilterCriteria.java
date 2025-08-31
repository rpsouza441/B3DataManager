package br.dev.rodrigopinheiro.B3DataManager.application.criteria;

import java.time.LocalDate;

/**
 * Critérios de filtro para operações.
 * Encapsula todos os filtros possíveis de forma tipada.
 */
public record FilterCriteria(
    String entradaSaida,
    LocalDate startDate,
    LocalDate endDate,
    String movimentacao,
    String produto,
    String instituicao,
    Boolean duplicado,
    Boolean dimensionado
) {}