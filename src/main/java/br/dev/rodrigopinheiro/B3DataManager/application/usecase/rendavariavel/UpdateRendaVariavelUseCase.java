package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendavariavel;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaVariavelRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateRendaVariavelUseCase {

    @Autowired
    private RendaVariavelRepositoryPort rendaVariavelRepository;

    @Autowired
    private GetRendaVariavelUseCase getRendaVariavelUseCase;

    public RendaVariavel execute(UpdateRendaVariavelRequest request) {
        // Validações de entrada
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID da renda variável é obrigatório");
        }
        if (request.getTicker() == null || request.getTicker().trim().isEmpty()) {
            throw new IllegalArgumentException("Ticker é obrigatório");
        }
        if (request.getTipoAtivoFinanceiroVariavel() == null) {
            throw new IllegalArgumentException("Tipo do ativo financeiro variável é obrigatório");
        }

        // Buscar a renda variável existente
        RendaVariavel rendaVariavel = getRendaVariavelUseCase.executeOrThrow(request.getId());

        // Atualizar os dados
        rendaVariavel.setTicker(request.getTicker().trim().toUpperCase());
        rendaVariavel.setTipoAtivoFinanceiroVariavel(request.getTipoAtivoFinanceiroVariavel());

        // Salvar e retornar
        return rendaVariavelRepository.save(rendaVariavel);
    }

    public static class UpdateRendaVariavelRequest {
        private Long id;
        private String ticker;
        private TipoAtivoFinanceiroVariavel tipoAtivoFinanceiroVariavel;

        public UpdateRendaVariavelRequest() {}

        public UpdateRendaVariavelRequest(Long id, String ticker, TipoAtivoFinanceiroVariavel tipoAtivoFinanceiroVariavel) {
            this.id = id;
            this.ticker = ticker;
            this.tipoAtivoFinanceiroVariavel = tipoAtivoFinanceiroVariavel;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public TipoAtivoFinanceiroVariavel getTipoAtivoFinanceiroVariavel() {
            return tipoAtivoFinanceiroVariavel;
        }

        public void setTipoAtivoFinanceiroVariavel(TipoAtivoFinanceiroVariavel tipoAtivoFinanceiroVariavel) {
            this.tipoAtivoFinanceiroVariavel = tipoAtivoFinanceiroVariavel;
        }
    }
}