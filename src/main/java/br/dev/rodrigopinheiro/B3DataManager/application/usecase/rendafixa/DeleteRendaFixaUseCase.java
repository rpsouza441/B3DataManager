package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendafixa;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteRendaFixaUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

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
        AtivoRendaFixa rendaFixa = getRendaFixaUseCase.executeOrThrow(rendaFixaId);

        // Verificar se possui transações associadas
        // Para SINGLE_TABLE, verificamos se há operações/transações relacionadas
        // Por simplicidade, vamos fazer exclusão lógica por padrão
        rendaFixa.setDeletado(true);
        rendaFixaRepository.save(rendaFixa);
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
        if (!ativoFinanceiroRepository.existsById(rendaFixaId)) {
            throw new IllegalArgumentException("Renda fixa não encontrada com ID: " + rendaFixaId);
        }

        // Exclusão física direta
        ativoFinanceiroRepository.deleteById(rendaFixaId);
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
        AtivoRendaFixa rendaFixa = getRendaFixaUseCase.executeOrThrow(rendaFixaId);

        // Exclusão lógica
        rendaFixa.setDeletado(true);
        ativoFinanceiroRepository.save(rendaFixa);
    }
}