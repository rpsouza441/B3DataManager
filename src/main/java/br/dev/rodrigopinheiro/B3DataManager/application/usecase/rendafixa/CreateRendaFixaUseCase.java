package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendafixa;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CreateRendaFixaUseCase {

    @Autowired
    private RendaFixaRepositoryPort rendaFixaRepository;

    @Autowired
    private AtivoFinanceiroRepositoryPort ativoFinanceiroRepository;

    public RendaFixa execute(CreateRendaFixaRequest request) {
        // Validações de entrada
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
        if (request.getAtivoFinanceiroId() == null) {
            throw new IllegalArgumentException("ID do ativo financeiro é obrigatório");
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

        // Verificar se o ativo financeiro existe
        AtivoFinanceiro ativoFinanceiro = ativoFinanceiroRepository.findById(request.getAtivoFinanceiroId())
                .orElseThrow(() -> new IllegalArgumentException("Ativo financeiro não encontrado com ID: " + request.getAtivoFinanceiroId()));

        // Verificar se o ativo não está deletado
        if (Boolean.TRUE.equals(ativoFinanceiro.getDeletado())) {
            throw new IllegalArgumentException("Não é possível criar renda fixa para um ativo deletado");
        }

        // Criar a renda fixa
        RendaFixa rendaFixa = new RendaFixa();
        rendaFixa.setTipoAtivoFinanceiroFixa(request.getTipoAtivoFinanceiroFixa());
        rendaFixa.setDataVencimento(request.getDataVencimento());
        rendaFixa.setTaxaJuros(request.getTaxaJuros());
        rendaFixa.setAtivoFinanceiro(ativoFinanceiro);
        rendaFixa.setDeletado(false);

        // Salvar e retornar
        return rendaFixaRepository.save(rendaFixa);
    }

    public static class CreateRendaFixaRequest {
        private TipoAtivoFinanceiroFixa tipoAtivoFinanceiroFixa;
        private LocalDate dataVencimento;
        private BigDecimal taxaJuros;
        private Long ativoFinanceiroId;

        public CreateRendaFixaRequest() {}

        public CreateRendaFixaRequest(TipoAtivoFinanceiroFixa tipoAtivoFinanceiroFixa, LocalDate dataVencimento, BigDecimal taxaJuros, Long ativoFinanceiroId) {
            this.tipoAtivoFinanceiroFixa = tipoAtivoFinanceiroFixa;
            this.dataVencimento = dataVencimento;
            this.taxaJuros = taxaJuros;
            this.ativoFinanceiroId = ativoFinanceiroId;
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

        public Long getAtivoFinanceiroId() {
            return ativoFinanceiroId;
        }

        public void setAtivoFinanceiroId(Long ativoFinanceiroId) {
            this.ativoFinanceiroId = ativoFinanceiro