package br.dev.rodrigopinheiro.B3DataManager.application.usecase.portfolio;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.PortfolioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CalculatePortfolioPercentagesUseCase {

    @Autowired
    private PortfolioRepositoryPort portfolioRepositoryPort;

    /**
     * Calcula os percentuais do portfolio.
     *
     * @param portfolioId ID do portfolio
     * @return Map com os percentuais calculados
     */
    public Map<String, BigDecimal> execute(Long portfolioId) {
        if (portfolioId == null) {
            throw new IllegalArgumentException("Portfolio ID não pode ser nulo");
        }

        Optional<Portfolio> portfolioOpt = portfolioRepositoryPort.findById(portfolioId);
        if (portfolioOpt.isEmpty()) {
            throw new IllegalArgumentException("Portfolio não encontrado com ID: " + portfolioId);
        }

        Portfolio portfolio = portfolioOpt.get();
        return calculatePercentages(portfolio);
    }

    /**
     * Calcula os percentuais do portfolio por usuário.
     *
     * @param usuarioId ID do usuário
     * @return Map com os percentuais calculados
     */
    public Map<String, BigDecimal> executeByUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuario ID não pode ser nulo");
        }

        Optional<Portfolio> portfolioOpt = portfolioRepositoryPort.findByUsuarioId(usuarioId)
                .stream()
                .findFirst();
        
        if (portfolioOpt.isEmpty()) {
            throw new IllegalArgumentException("Portfolio não encontrado para usuário ID: " + usuarioId);
        }

        Portfolio portfolio = portfolioOpt.get();
        return calculatePercentages(portfolio);
    }

    private Map<String, BigDecimal> calculatePercentages(Portfolio portfolio) {
        Map<String, BigDecimal> percentages = new HashMap<>();
        
        BigDecimal saldoTotal = portfolio.getSaldoTotal() != null ? portfolio.getSaldoTotal() : BigDecimal.ZERO;
        BigDecimal saldoAplicado = portfolio.getSaldoAplicado() != null ? portfolio.getSaldoAplicado() : BigDecimal.ZERO;
        BigDecimal lucroVenda = portfolio.getLucroVenda() != null ? portfolio.getLucroVenda() : BigDecimal.ZERO;
        BigDecimal lucroRendimento = portfolio.getLucroRendimento() != null ? portfolio.getLucroRendimento() : BigDecimal.ZERO;
        
        // Evita divisão por zero
        if (saldoTotal.compareTo(BigDecimal.ZERO) == 0) {
            percentages.put("percentualAplicado", BigDecimal.ZERO);
            percentages.put("percentualLucroVenda", BigDecimal.ZERO);
            percentages.put("percentualLucroRendimento", BigDecimal.ZERO);
            percentages.put("rentabilidadeTotal", BigDecimal.ZERO);
        } else {
            // Calcula percentuais
            BigDecimal percentualAplicado = saldoAplicado
                    .divide(saldoTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            BigDecimal percentualLucroVenda = lucroVenda
                    .divide(saldoTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            BigDecimal percentualLucroRendimento = lucroRendimento
                    .divide(saldoTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            // Rentabilidade total = (saldo total - saldo aplicado) / saldo aplicado * 100
            BigDecimal rentabilidadeTotal = BigDecimal.ZERO;
            if (saldoAplicado.compareTo(BigDecimal.ZERO) > 0) {
                rentabilidadeTotal = saldoTotal.subtract(saldoAplicado)
                        .divide(saldoAplicado, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            
            percentages.put("percentualAplicado", percentualAplicado);
            percentages.put("percentualLucroVenda", percentualLucroVenda);
            percentages.put("percentualLucroRendimento", percentualLucroRendimento);
            percentages.put("rentabilidadeTotal", rentabilidadeTotal);
        }
        
        // Adiciona valores absolutos também
        percentages.put("saldoTotal", saldoTotal);
        percentages.put("saldoAplicado", saldoAplicado);
        percentages.put("lucroVenda", lucroVenda);
        percentages.put("lucroRendimento", lucroRendimento);
        
        return percentages;
    }
}