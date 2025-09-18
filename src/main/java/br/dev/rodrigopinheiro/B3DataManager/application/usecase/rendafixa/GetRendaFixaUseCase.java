package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendafixa;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GetRendaFixaUseCase {

    @Autowired
    private RendaFixaRepositoryPort rendaFixaRepository;
    
    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    public Optional<AtivoRendaFixa> execute(Long rendaFixaId) {
        // Validações de entrada
        if (rendaFixaId == null) {
            throw new IllegalArgumentException("ID da renda fixa é obrigatório");
        }
        if (rendaFixaId <= 0) {
            throw new IllegalArgumentException("ID da renda fixa deve ser um número positivo");
        }

        // Buscar diretamente o ativo de renda fixa
        return rendaFixaRepository.findById(rendaFixaId)
                .filter(a -> !Boolean.TRUE.equals(a.getDeletado()));
    }

    public AtivoRendaFixa executeOrThrow(Long rendaFixaId) {
        return execute(rendaFixaId)
                .orElseThrow(() -> new IllegalArgumentException("Renda fixa não encontrada com ID: " + rendaFixaId));
    }

    public List<AtivoRendaFixa> executeByAtivoFinanceiro(Long ativoFinanceiroId) {
        return executeByAtivoFinanceiro(ativoFinanceiroId, false);
    }

    public List<AtivoRendaFixa> executeByAtivoFinanceiro(Long ativoFinanceiroId, boolean includeDeleted) {
        // Validações de entrada
        if (ativoFinanceiroId == null) {
            throw new IllegalArgumentException("ID do ativo financeiro é obrigatório");
        }
        if (ativoFinanceiroId <= 0) {
            throw new IllegalArgumentException("ID do ativo financeiro deve ser um número positivo");
        }

        // Com SINGLE_TABLE, um ativo específico já é do tipo correto
        // Buscar o ativo e verificar se é renda fixa
        Optional<AtivoFinanceiro> ativo = ativoFinanceiroRepository.findById(ativoFinanceiroId);
        
        if (ativo.isPresent() && TipoAtivo.RENDA_FIXA.equals(ativo.get().getTipoAtivo())) {
            if (includeDeleted || !Boolean.TRUE.equals(ativo.get().getDeletado())) {
                return List.of((AtivoRendaFixa) ativo.get());
            }
        }
        
        return List.of();
    }

    public List<AtivoRendaFixa> executeAll() {
        return executeAll(false);
    }

    public List<AtivoRendaFixa> executeAll(boolean includeDeleted) {
        // Buscar todos os ativos de renda fixa
        List<AtivoFinanceiro> ativos = ativoFinanceiroRepository.findByTipoAtivo(TipoAtivo.RENDA_FIXA);

        // Filtrar deletados se necessário e converter para AtivoRendaFixa
        return ativos.stream()
                .filter(a -> includeDeleted || !Boolean.TRUE.equals(a.getDeletado()))
                .map(a -> (AtivoRendaFixa) a)
                .collect(Collectors.toList());
    }
}