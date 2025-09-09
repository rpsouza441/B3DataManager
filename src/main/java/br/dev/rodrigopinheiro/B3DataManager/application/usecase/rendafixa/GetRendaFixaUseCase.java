package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendafixa;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GetRendaFixaUseCase {

    @Autowired
    private RendaFixaRepositoryPort rendaFixaRepository;

    public Optional<RendaFixa> execute(Long rendaFixaId) {
        // Validações de entrada
        if (rendaFixaId == null) {
            throw new IllegalArgumentException("ID da renda fixa é obrigatório");
        }
        if (rendaFixaId <= 0) {
            throw new IllegalArgumentException("ID da renda fixa deve ser um número positivo");
        }

        // Buscar a renda fixa
        Optional<RendaFixa> rendaFixa = rendaFixaRepository.findById(rendaFixaId);
        
        // Filtrar rendas fixas deletadas
        return rendaFixa.filter(rf -> !Boolean.TRUE.equals(rf.getDeletado()));
    }

    public RendaFixa executeOrThrow(Long rendaFixaId) {
        return execute(rendaFixaId)
                .orElseThrow(() -> new IllegalArgumentException("Renda fixa não encontrada com ID: " + rendaFixaId));
    }

    public List<RendaFixa> executeByAtivoFinanceiro(Long ativoFinanceiroId) {
        return executeByAtivoFinanceiro(ativoFinanceiroId, false);
    }

    public List<RendaFixa> executeByAtivoFinanceiro(Long ativoFinanceiroId, boolean includeDeleted) {
        // Validações de entrada
        if (ativoFinanceiroId == null) {
            throw new IllegalArgumentException("ID do ativo financeiro é obrigatório");
        }
        if (ativoFinanceiroId <= 0) {
            throw new IllegalArgumentException("ID do ativo financeiro deve ser um número positivo");
        }

        // Buscar as rendas fixas do ativo
        List<RendaFixa> rendasFixas = rendaFixaRepository.findByAtivoFinanceiroId(ativoFinanceiroId);

        // Filtrar rendas fixas deletadas se necessário
        if (!includeDeleted) {
            rendasFixas = rendasFixas.stream()
                    .filter(rf -> !Boolean.TRUE.equals(rf.getDeletado()))
                    .collect(Collectors.toList());
        }

        return rendasFixas;
    }

    public List<RendaFixa> executeAll() {
        return executeAll(false);
    }

    public List<RendaFixa> executeAll(boolean includeDeleted) {
        List<RendaFixa> rendasFixas = rendaFixaRepository.findAll();

        // Filtrar rendas fixas deletadas se necessário
        if (!includeDeleted) {
            rendasFixas = rendasFixas.stream()
                    .filter(rf -> !Boolean.TRUE.equals(rf.getDeletado()))
                    .collect(Collectors.toList());
        }

        return rendas