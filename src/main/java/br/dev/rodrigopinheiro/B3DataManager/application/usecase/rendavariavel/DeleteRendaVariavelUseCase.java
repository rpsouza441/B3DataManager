package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendavariavel;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaVariavelRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteRendaVariavelUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    @Autowired
    private RendaVariavelRepositoryPort rendaVariavelRepository;

    @Autowired
    private GetRendaVariavelUseCase getRendaVariavelUseCase;

    public void execute(Long rendaVariavelId) {
        // Validações de entrada
        if (rendaVariavelId == null) {
            throw new IllegalArgumentException("ID da renda variável é obrigatório");
        }
        if (rendaVariavelId <= 0) {
            throw new IllegalArgumentException("ID da renda variável deve ser um número positivo");
        }

        // Buscar a renda variável existente
        RendaVariavel rendaVariavel = getRendaVariavelUseCase.executeOrThrow(rendaVariavelId);

        // Verificar se possui transações associadas através do ativo financeiro
        if (rendaVariavel.getAtivoFinanceiro() != null) {
            // Fazer exclusão lógica no ativo financeiro
            rendaVariavel.getAtivoFinanceiro().setDeletado(true);
            ativoFinanceiroRepository.save(rendaVariavel.getAtivoFinanceiro());
        } else {
            // Se não possui ativo financeiro associado, pode fazer exclusão física
            rendaVariavelRepository.deleteById(rendaVariavelId);
        }
    }

    public void executePhysical(Long rendaVariavelId) {
        // Validações de entrada
        if (rendaVariavelId == null) {
            throw new IllegalArgumentException("ID da renda variável é obrigatório");
        }
        if (rendaVariavelId <= 0) {
            throw new IllegalArgumentException("ID da renda variável deve ser um número positivo");
        }

        // Verificar se a renda variável existe
        if (!rendaVariavelRepository.existsById(rendaVariavelId)) {
            throw new IllegalArgumentException("Renda variável não encontrada com ID: " + rendaVariavelId);
        }

        // Exclusão física direta
        rendaVariavelRepository.deleteById(rendaVariavelId);
    }

    public void executeLogical(Long rendaVariavelId) {
        // Validações de entrada
        if (rendaVariavelId == null) {
            throw new IllegalArgumentException("ID da renda variável é obrigatório");
        }
        if (rendaVariavelId <= 0) {
            throw new IllegalArgumentException("ID da renda variável deve ser um número positivo");
        }

        // Buscar a renda variável existente
        RendaVariavel rendaVariavel = getRendaVariavelUseCase.executeOrThrow(rendaVariavelId);

        // Exclusão lógica no ativo financeiro
        if (rendaVariavel.getAtivoFinanceiro() != null) {
            rendaVariavel.getAtivoFinanceiro().setDeletado(true);
            ativoFinanceiroRepository.save(rendaVariavel.getAtivoFinanceiro());
        }
    }
}