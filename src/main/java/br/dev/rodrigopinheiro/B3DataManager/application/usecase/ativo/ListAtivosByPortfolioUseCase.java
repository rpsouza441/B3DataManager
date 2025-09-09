package br.dev.rodrigopinheiro.B3DataManager.application.usecase.ativo;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.PortfolioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListAtivosByPortfolioUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    @Autowired
    private PortfolioRepositoryPort portfolioRepository;

    public List<AtivoFinanceiro> execute(Long portfolioId) {
        return execute(portfolioId, false);
    }

    public List<AtivoFinanceiro> execute(Long portfolioId, boolean includeDeleted) {
        // Validações de entrada
        if (portfolioId == null) {
            throw new IllegalArgumentException("ID do portfolio é obrigatório");
        }
        if (portfolioId <= 0) {
            throw new IllegalArgumentException("ID do portfolio deve ser um número positivo");
        }

        // Verificar se o portfolio existe
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new IllegalArgumentException("Portfolio não encontrado com ID: " + portfolioId);
        }

        // Buscar os ativos do portfolio
        List<AtivoFinanceiro> ativos = ativoFinanceiroRepository.findByPortfolioId(portfolioId);

        // Filtrar ativos deletados se necessário
        if (!includeDeleted) {
            ativos = ativos.stream()
                    .filter(ativo -> !ativo.getDeletado())
                    .collect(Collectors.toList());
        }

        return ativos;
    }

    public List<AtivoFinanceiro> executeByUsuarioId(Long usuarioId) {
        return executeByUsuarioId(usuarioId, false);
    }

    public List<AtivoFinanceiro> executeByUsuarioId(Long usuarioId, boolean includeDeleted) {
        // Validações de entrada
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser um número positivo");
        }

        // Buscar o portfolio do usuário
        List<br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio> portfolios = portfolioRepository.findByUsuarioId(usuarioId);
        
        if (portfolios.isEmpty()) {
            throw new IllegalArgumentException("Nenhum portfolio encontrado para o usuário com ID: " + usuarioId);
        }

        // Assumindo que cada usuário tem apenas um portfolio
        Long portfolioId = portfolios.get(0).getId();
        
        return execute(portfolioId, includeDeleted);
    }
}