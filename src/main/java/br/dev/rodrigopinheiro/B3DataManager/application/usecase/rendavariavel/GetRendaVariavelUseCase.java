package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendavariavel;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaVariavelRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GetRendaVariavelUseCase {

    @Autowired
    private RendaVariavelRepositoryPort rendaVariavelRepository;

    public Optional<RendaVariavel> execute(Long rendaVariavelId) {
        // Validações de entrada
        if (rendaVariavelId == null) {
            throw new IllegalArgumentException("ID da renda variável é obrigatório");
        }
        if (rendaVariavelId <= 0) {
            throw new IllegalArgumentException("ID da renda variável deve ser um número positivo");
        }

        // Buscar a renda variável
        Optional<RendaVariavel> rendaVariavel = rendaVariavelRepository.findById(rendaVariavelId);
        
        // Filtrar rendas variáveis deletadas
        return rendaVariavel.filter(rv -> !Boolean.TRUE.equals(rv.getDeletado()));
    }

    public RendaVariavel executeOrThrow(Long rendaVariavelId) {
        return execute(rendaVariavelId)
                .orElseThrow(() -> new IllegalArgumentException("Renda variável não encontrada com ID: " + rendaVariavelId));
    }

    public List<RendaVariavel> executeByAtivoFinanceiro(Long ativoFinanceiroId) {
        return executeByAtivoFinanceiro(ativoFinanceiroId, false);
    }

    public List<RendaVariavel> executeByAtivoFinanceiro(Long ativoFinanceiroId, boolean includeDeleted) {
        // Validações de entrada
        if (ativoFinanceiroId == null) {
            throw new IllegalArgumentException("ID do ativo financeiro é obrigatório");
        }
        if (ativoFinanceiroId <= 0) {
            throw new IllegalArgumentException("ID do ativo financeiro deve ser um número positivo");
        }

        // Buscar as rendas variáveis do ativo
        List<RendaVariavel> rendasVariaveis = rendaVariavelRepository.findByAtivoFinanceiroId(ativoFinanceiroId);

        // Filtrar rendas variáveis deletadas se necessário
        if (!includeDeleted) {
            rendasVariaveis = rendasVariaveis.stream()
                    .filter(rv -> !Boolean.TRUE.equals(rv.getDeletado()))
                    .collect(Collectors.toList());
        }

        return rendasVariaveis;
    }

    public List<RendaVariavel> executeAll() {
        return executeAll(false);
    }

    public List<RendaVariavel> executeAll(boolean includeDeleted) {
        List<RendaVariavel> rendasVariaveis = rendaVariavelRepository.findAll();

        // Filtrar rendas variáveis deletadas se necessário
        if (!includeDeleted) {
            rendasVariaveis = rendasVariaveis.stream()
                    .filter(rv -> !Boolean.TRUE.equals(rv.getDeletado()))
                    .collect(Collectors.toList());
        }

        return rendasVariaveis