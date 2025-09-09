package br.dev.rodrigopinheiro.B3DataManager.application.usecase.ativo;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteAtivoFinanceiroUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    @Autowired
    private GetAtivoFinanceiroUseCase getAtivoFinanceiroUseCase;

    public void execute(Long ativoId) {
        // Validações de entrada
        if (ativoId == null) {
            throw new IllegalArgumentException("ID do ativo é obrigatório");
        }
        if (ativoId <= 0) {
            throw new IllegalArgumentException("ID do ativo deve ser um número positivo");
        }

        // Buscar o ativo existente
        AtivoFinanceiro ativo = getAtivoFinanceiroUseCase.executeOrThrow(ativoId);

        // Verificar se o ativo possui transações associadas
        if (ativo.getTransacoes() != null && !ativo.getTransacoes().isEmpty()) {
            // Se possui transações, fazer exclusão lógica
            ativo.setDeletado(true);
            ativoFinanceiroRepository.save(ativo);
        } else {
            // Se não possui transações, pode fazer exclusão física
            ativoFinanceiroRepository.deleteById(ativoId);
        }
    }

    public void executePhysical(Long ativoId) {
        // Validações de entrada
        if (ativoId == null) {
            throw new IllegalArgumentException("ID do ativo é obrigatório");
        }
        if (ativoId <= 0) {
            throw new IllegalArgumentException("ID do ativo deve ser um número positivo");
        }

        // Verificar se o ativo existe
        if (!ativoFinanceiroRepository.existsById(ativoId)) {
            throw new IllegalArgumentException("Ativo financeiro não encontrado com ID: " + ativoId);
        }

        // Exclusão física direta
        ativoFinanceiroRepository.deleteById(ativoId);
    }
}