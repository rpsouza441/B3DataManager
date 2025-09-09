package br.dev.rodrigopinheiro.B3DataManager.application.usecase.ativo;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetAtivoFinanceiroUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    public Optional<AtivoFinanceiro> execute(Long ativoId) {
        // Validações de entrada
        if (ativoId == null) {
            throw new IllegalArgumentException("ID do ativo é obrigatório");
        }
        if (ativoId <= 0) {
            throw new IllegalArgumentException("ID do ativo deve ser um número positivo");
        }

        // Buscar o ativo
        Optional<AtivoFinanceiro> ativo = ativoFinanceiroRepository.findById(ativoId);
        
        // Filtrar ativos deletados
        return ativo.filter(a -> !a.getDeletado());
    }

    public AtivoFinanceiro executeOrThrow(Long ativoId) {
        return execute(ativoId)
                .orElseThrow(() -> new IllegalArgumentException("Ativo financeiro não encontrado com ID: " + ativoId));
    }
}