package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendafixa;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class UpdateRendaFixaUseCase {

    @Autowired
    private RendaFixaRepositoryPort rendaFixaRepository;

    @Autowired
    private GetRendaFixaUseCase getRendaFixaUseCase;

    public AtivoRendaFixa execute(UpdateRendaFixaRequest request) {
        // Validações de entrada
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID da renda fixa é obrigatório");
        }
        if (request.getTipoAtivoFinanceiroFixa() == null) {
            throw new IllegalArgumentException("Tipo do ativo financeiro fixa é obrigatório");
        }
        if (request.getDataVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória");
        }
        if (request.getDataVencimento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento deve ser futura");
        }
        if (request.getTaxaJuros() == null || request.getTaxaJuros().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de juros deve ser um valor positivo");
        }

        // Buscar a renda fixa existente
        AtivoRendaFixa rendaFixa = getRendaFixaUseCase.executeOrThrow(request.getId());

        // Atualizar os dados - usando métodos corretos da classe AtivoRendaFixa
        rendaFixa.setTipoRendaFixa(request.getTipoAtivoFinanceiroFixa());
        rendaFixa.setDataVencimento(request.getDataVencimento());
        rendaFixa.setTaxaJuros(request.getTaxaJuros());

        // Salvar e retornar
        return rendaFixaRepository.save(rendaFixa);
    }

    public static class UpdateRendaFixaRequest {
        private Long id;
        private TipoAtivoFinanceiroFixa tipoAtivoFinanceiroFixa;
        private LocalDate dataVencimento;
        private BigDecimal taxaJuros;

        public UpdateRendaFixaRequest() {}

        public UpdateRendaFixaRequest(Long id, TipoAtivoFinanceiroFixa tipoAtivoFinanceiroFixa, LocalDate dataVencimento, BigDecimal taxaJuros) {
            this.id = id;
            this.tipoAtivoFinanceiroFixa = tipoAtivoFinanceiroFixa;
            this.dataVencimento = dataVencimento;
            this.taxaJuros = taxaJuros;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public TipoAtivoFinanceiroFixa getTipoAtivoFinanceiroFixa() {
            return tipoAtivoFinanceiroFixa;
        }

        public void setTipoAtivoFinanceiroFixa(TipoAtivoFinanceiroFixa tipoAtivoFinanceiroFixa) {
            this.tipoAtivoFinanceiroFixa = tipoAtivoFinanceiroFixa;
        }

        public LocalDate getDataVencimento() {
            return dataVencimento;
        }

        public void setDataVencimento(LocalDate dataVencimento) {
            this.dataVencimento = dataVencimento;
        }

        public BigDecimal getTaxaJuros() {
            return taxaJuros;
        }

        public void setTaxaJuros(BigDecimal taxaJuros) {
            this.taxaJuros = taxaJuros;
        }
    }
}