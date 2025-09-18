package br.dev.rodrigopinheiro.B3DataManager.application.usecase.rendafixa;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
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

    public AtivoRendaFixa execute(CreateRendaFixaRequest request) {
        // Validações de entrada
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
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

        // Criar novo ativo de renda fixa
        AtivoRendaFixa ativoRendaFixa = new AtivoRendaFixa();
        ativoRendaFixa.setTipoRendaFixa(request.getTipoAtivoFinanceiroFixa());
        ativoRendaFixa.setDataVencimento(request.getDataVencimento());
        ativoRendaFixa.setTaxaJuros(request.getTaxaJuros());
        
        // Se foi fornecido um ID de ativo financeiro, buscar e atualizar
        if (request.getAtivoFinanceiroId() != null) {
            AtivoFinanceiro ativoExistente = ativoFinanceiroRepository.findById(request.getAtivoFinanceiroId())
                    .orElseThrow(() -> new IllegalArgumentException("Ativo financeiro não encontrado com ID: " + request.getAtivoFinanceiroId()));

            // Verificar se o ativo não está deletado
            if (Boolean.TRUE.equals(ativoExistente.getDeletado())) {
                throw new IllegalArgumentException("Ativo financeiro está deletado");
            }

            // Verificar se o ativo já é do tipo renda fixa
            if (!TipoAtivo.RENDA_FIXA.equals(ativoExistente.getTipoAtivo())) {
                throw new IllegalArgumentException("O ativo financeiro deve ser do tipo RENDA_FIXA");
            }

            // Cast seguro para AtivoRendaFixa
            AtivoRendaFixa ativoExistenteRF = (AtivoRendaFixa) ativoExistente;
            ativoExistenteRF.setTipoRendaFixa(request.getTipoAtivoFinanceiroFixa());
            ativoExistenteRF.setDataVencimento(request.getDataVencimento());
            ativoExistenteRF.setTaxaJuros(request.getTaxaJuros());
            
            return rendaFixaRepository.save(ativoExistenteRF);
        }

        // Salvar novo ativo
        return rendaFixaRepository.save(ativoRendaFixa);
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
            this.ativoFinanceiroId = ativoFinanceiroId;
        }
    }
}