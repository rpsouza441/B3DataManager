package br.dev.rodrigopinheiro.B3DataManager.application.usecase.ativo;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateAtivoFinanceiroUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    @Autowired
    private GetAtivoFinanceiroUseCase getAtivoFinanceiroUseCase;

    public AtivoFinanceiro execute(UpdateAtivoFinanceiroRequest request) {
        // Validações de entrada
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID do ativo é obrigatório");
        }
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do ativo é obrigatório");
        }

        // Buscar o ativo existente
        AtivoFinanceiro ativo = getAtivoFinanceiroUseCase.executeOrThrow(request.getId());

        // Verificar se o novo nome já existe em outro ativo do mesmo portfolio
        if (!ativo.getNome().equals(request.getNome())) {
            if (ativoFinanceiroRepository.existsByNomeAndPortfolioId(request.getNome(), ativo.getPortfolio().getId())) {
                throw new IllegalArgumentException("Já existe um ativo com o nome '" + request.getNome() + "' neste portfolio");
            }
        }

        // Atualizar os dados
        ativo.setNome(request.getNome());

        // Salvar e retornar
        return ativoFinanceiroRepository.save(ativo);
    }

    public static class UpdateAtivoFinanceiroRequest {
        private Long id;
        private String nome;

        public UpdateAtivoFinanceiroRequest() {}

        public UpdateAtivoFinanceiroRequest(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this