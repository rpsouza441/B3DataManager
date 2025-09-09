package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendafixa;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteRendaFixaUseCase {

    @Autowired
    private RendaFixaRepositoryPort rendaFixaRepository;

    @Autowired
    private GetRendaFixaUseCase getRendaFixaUseCase;

    public void execute(Long rendaFixaId) {
        // Validações de entrada
        if (rendaFixaId == null) {
            throw new IllegalArgumentException("ID da renda fixa é obrigatório");
        }
        if (rendaFixaId <= 0) {
            throw new IllegalArgumentException("ID da renda fixa deve ser um número positivo");
        }

        // Buscar a renda fixa existente
        RendaFixa rendaFixa = getRendaFixaUseCase.executeOrThrow(rendaFixaId);

        // Verificar se possui transações associadas através do ativo financeiro
        if (rendaFixa.getAtivoFinanceiro() != null && 
            rendaFixa.getAtivoFinanceiro().getTransacoes() != null && 
            !rendaFixa.getAtivoFinanceiro().getTransacoes().isEmpty()) {
            // Se possui transações, fazer exclusão lógica
            rendaFixa.setDeletado(true);
            rendaFixaRepository.save(rendaFixa);
        } else {
            // Se não possui transações, pode fazer exclusão física
            rendaFixaRepository.deleteById(rendaFixaId);
        }
    }

    public void executePhysical(Long rendaFixaId) {
        // Validações de entrada
        if (rendaFixaId == null) {
            throw new IllegalArgumentException("ID da renda fixa é obrigatório");
        }
        if (rendaFixaId <= 0) {
            throw new IllegalArgumentException("ID da renda fixa deve ser um número positivo");
        }

        // Verificar se a renda fixa existe
        if (!rendaFixaRepository.existsById(rendaFixaId)) {
            throw new IllegalArgumentException("Renda fixa não encontrada com ID: " + rendaFixaId);
        }

        // Exclusão física direta
        rendaFixaRepository.deleteById(rendaFixaId);
    }

    public void executeLogical(Long rendaFixaId) {
        // Validações de entrada
        if (rendaFixaId == null) {
            throw new IllegalArgumentException("ID da renda fixa é obrigatório");
        }
        if (rendaFixaId <= 0) {
            throw new IllegalArgumentException("ID da renda fixa deve ser um número positivo");
        }

        // Buscar a renda fixa existente
        RendaFixa rendaFixa = getRendaFixaUseCase.executeOrThrow(rendaFixaId);

        // Exclusão lógica
        rendaFixa.setDeletado(true);
        renda