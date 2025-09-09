package br.dev.rodrigopinheiro.B3DataManager.application.usecase.ativo;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.PortfolioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateAtivoFinanceiroUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    @Autowired
    private PortfolioRepositoryPort portfolioRepository;

    public AtivoFinanceiro execute(CreateAtivoFinanceiroRequest request) {
        // Validações de entrada
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do ativo é obrigatório");
        }
        if (request.getPortfolioId() == null) {
            throw new IllegalArgumentException("ID do portfolio é obrigatório");
        }

        // Verificar se o portfolio existe
        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new IllegalArgumentException("Portfolio não encontrado com ID: " + request.getPortfolioId()));

        // Verificar se já existe um ativo com o mesmo nome no portfolio
        if (ativoFinanceiroRepository.existsByNomeAndPortfolioId(request.getNome(), request.getPortfolioId())) {
            throw new IllegalArgumentException("Já existe um ativo com o nome '" + request.getNome() + "' neste portfolio");
        }

        // Criar o ativo financeiro
        AtivoFinanceiro ativo = new AtivoFinanceiro();
        ativo.setNome(request.getNome());
        ativo.setPortfolio(portfolio);
        ativo.setDeletado(false);

        // Salvar e retornar
        return ativoFinanceiroRepository.save(ativo);
    }

    public static class CreateAtivoFinanceiroRequest {
        private String nome;
        private Long portfolioId;

        public CreateAtivoFinanceiroRequest() {}

        public CreateAtivoFinanceiroRequest(String nome, Long portfolioId) {
            this.nome = nome;
            this.portfolioId = portfolioId;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Long getPortfolioId() {
            return portfolioId;
        }

        public void setPortfolioId(Long portfolioId) {
            this.portfolioId = portfolioId;
        }
    }
}