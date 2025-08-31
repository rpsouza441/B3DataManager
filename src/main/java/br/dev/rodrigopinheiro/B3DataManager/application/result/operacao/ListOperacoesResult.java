package br.dev.rodrigopinheiro.B3DataManager.application.result.operacao;

import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.OperacaoDTO;

import java.util.List;

/**
 * Result da listagem de operações com informações de paginação.
 */
public record ListOperacoesResult(
    List<OperacaoDTO> operacoes,
    int totalPages,
    long totalElements,
    int currentPage,
    int pageSize
) {}