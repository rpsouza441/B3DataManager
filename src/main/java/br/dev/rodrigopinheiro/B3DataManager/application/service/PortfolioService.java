package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.PortfolioRepository;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, UsuarioRepository usuarioRepository) {
        this.portfolioRepository = portfolioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtém o Portfolio do usuário, caso exista, ou cria um novo se não encontrado.
     *
     * @param usuarioId ID do usuário.
     * @return Portfolio associado ao usuário.
     */
    @Transactional
    public Portfolio obterOuCriarPortfolio(Long usuarioId) {
        Optional<Portfolio> optionalPortfolio = portfolioRepository.findByUsuarioId(usuarioId);
        if (optionalPortfolio.isPresent()) {
            log.info("Portfolio encontrado para o usuário {}: {}", usuarioId, optionalPortfolio.get());

            return optionalPortfolio.get();
        }
        log.info("Portfolio não encontrado para o usuário {}. Criando novo portfolio.", usuarioId);

        // Recupera o usuário; se não existir, lança uma exceção
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + usuarioId));
        // Cria um novo Portfolio e associa ao usuário
        Portfolio portfolio = new Portfolio();
        portfolio.setUsuario(usuario);
        // Inicializa os saldos com valores padrão (zero)
        portfolio.setSaldoTotal(BigDecimal.ZERO);
        portfolio.setSaldoAplicado(BigDecimal.ZERO);
        portfolio.setLucroVenda(BigDecimal.ZERO);
        portfolio.setLucroRendimento(BigDecimal.ZERO);
        // Salva e retorna o novo Portfolio
        Portfolio novoPortfolio = portfolioRepository.save(portfolio);
        log.info("Novo portfolio criado para o usuário {}: {}", usuarioId, novoPortfolio);
        return novoPortfolio;
    }
}
