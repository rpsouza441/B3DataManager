package br.dev.rodrigopinheiro.B3DataManager.application.usecase.portfolio;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.PortfolioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetPortfolioUseCase {

    @Autowired
    private PortfolioRepositoryPort portfolioRepositoryPort;

    /**
     * Busca um portfolio por ID.
     *
     * @param portfolioId ID do portfolio
     * @return Portfolio encontrado ou Optional.empty()
     */
    public Optional<Portfolio> execute(Long portfolioId) {
        if (portfolioId == null) {
            throw new IllegalArgumentException("Portfolio ID não pode ser nulo");
        }
        
        return portfolioRepositoryPort.findById(portfolioId);
    }

    /**
     * Busca o portfolio de um usuário.
     *
     * @param usuarioId ID do usuário
     * @return Portfolio do usuário ou Optional.empty()
     */
    public Optional<Portfolio> executeByUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuario ID não pode ser nulo");
        }
        
        return portfolioRepositoryPort.findByUsuarioId(usuarioId)
                .stream()
                .findFirst();
    }
}