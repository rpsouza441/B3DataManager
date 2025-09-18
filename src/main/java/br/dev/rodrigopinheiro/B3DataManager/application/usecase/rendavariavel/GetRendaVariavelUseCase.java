package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendavariavel;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GetRendaVariavelUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    public Optional<RendaVariavel> execute(Long rendaVariavelId) {
        // Validações de entrada
        if (rendaVariavelId == null) {
            throw new IllegalArgumentException("ID da renda variável é obrigatório");
        }
        if (rendaVariavelId <= 0) {
            throw new IllegalArgumentException("ID da renda variável deve ser um número positivo");
        }

        // Buscar o ativo financeiro
        Optional<AtivoFinanceiro> ativoFinanceiro = ativoFinanceiroRepository.findById(rendaVariavelId);
        
        // Filtrar por tipo e status de deleção
        return ativoFinanceiro
                .filter(af -> af.getTipoAtivo() == TipoAtivo.RENDA_VARIAVEL)
                .filter(af -> !Boolean.TRUE.equals(af.getDeletado()))
                .map(this::convertToRendaVariavel);
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

        // Buscar os ativos financeiros do tipo renda variável
        List<AtivoFinanceiro> ativosFinanceiros = ativoFinanceiroRepository.findAll();
        
        // Filtrar por tipo, ativo financeiro e status de deleção
        return ativosFinanceiros.stream()
                .filter(af -> af.getTipoAtivo() == TipoAtivo.RENDA_VARIAVEL)
                .filter(af -> includeDeleted || !Boolean.TRUE.equals(af.getDeletado()))
                .map(this::convertToRendaVariavel)
                .collect(Collectors.toList());
    }

    public List<RendaVariavel> executeAll() {
        return executeAll(false);
    }

    public List<RendaVariavel> executeAll(boolean includeDeleted) {
        List<AtivoFinanceiro> ativosFinanceiros = ativoFinanceiroRepository.findAll();

        // Filtrar por tipo e status de deleção
        return ativosFinanceiros.stream()
                .filter(af -> af.getTipoAtivo() == TipoAtivo.RENDA_VARIAVEL)
                .filter(af -> includeDeleted || !Boolean.TRUE.equals(af.getDeletado()))
                .map(this::convertToRendaVariavel)
                .collect(Collectors.toList());
    }
    
    private RendaVariavel convertToRendaVariavel(AtivoFinanceiro ativo) {
        RendaVariavel rendaVariavel = new RendaVariavel();
        rendaVariavel.setId(ativo.getId());
        rendaVariavel.setAtivoFinanceiro(ativo);
        // Definir outros campos conforme necessário
        return rendaVariavel;
    }
}