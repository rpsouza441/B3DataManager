package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendavariavel;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateRendaVariavelUseCase {

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    public RendaVariavel execute(CreateRendaVariavelRequest request) {
        // Validações de entrada
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
        if (request.getTicker() == null || request.getTicker().trim().isEmpty()) {
            throw new IllegalArgumentException("Ticker é obrigatório");
        }
        if (request.getAtivoFinanceiroId() == null) {
            throw new IllegalArgumentException("ID do ativo financeiro é obrigatório");
        }
        if (request.getTipoAtivoFinanceiroVariavel() == null) {
            throw new IllegalArgumentException("Tipo do ativo financeiro variável é obrigatório");
        }

        // Verificar se o ativo financeiro existe
        AtivoFinanceiro ativoFinanceiro = ativoFinanceiroRepository.findById(request.getAtivoFinanceiroId())
                .orElseThrow(() -> new IllegalArgumentException("Ativo financeiro não encontrado com ID: " + request.getAtivoFinanceiroId()));

        // Verificar se o ativo não está deletado
        if (Boolean.TRUE.equals(ativoFinanceiro.getDeletado())) {
            throw new IllegalArgumentException("Não é possível criar renda variável para um ativo deletado");
        }

        // Verificar se o ativo é do tipo renda variável
        if (ativoFinanceiro.getTipoAtivo() != TipoAtivo.RENDA_VARIAVEL) {
            throw new IllegalArgumentException("O ativo financeiro deve ser do tipo RENDA_VARIAVEL");
        }

        // Criar uma nova instância de RendaVariavel
        RendaVariavel rendaVariavel = new RendaVariavel();
        rendaVariavel.setAtivoFinanceiro(ativoFinanceiro);
        return rendaVariavel;
    }

    public static class CreateRendaVariavelRequest {
        private String ticker;
        private TipoAtivoFinanceiroVariavel tipoAtivoFinanceiroVariavel;
        private Long ativoFinanceiroId;

        public CreateRendaVariavelRequest() {}

        public CreateRendaVariavelRequest(String ticker, TipoAtivoFinanceiroVariavel tipoAtivoFinanceiroVariavel, Long ativoFinanceiroId) {
            this.ticker = ticker;
            this.tipoAtivoFinanceiroVariavel = tipoAtivoFinanceiroVariavel;
            this.ativoFinanceiroId = ativoFinanceiroId;
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

        public Long getAtivoFinanceiroId() {
            return ativoFinanceiroId;
        }

        public void setAtivoFinanceiroId(Long ativoFinanceiroId) {
            this.ativoFinanceiroId = ativoFinanceiroId;
        }
    }
}