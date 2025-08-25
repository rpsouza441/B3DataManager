package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoMovimentacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;


/**
 * Implementação concreta da interface IMovimentacaoTypeResolverService.
 *
 * Este serviço é responsável por determinar o tipo de movimentação de uma transação com base nos dados da operação.
 */
@Slf4j
@Service
public class TipoMovimentacaoMapperImpl implements TipoMovimentacaoMapper {

    /**
     * Determina o tipo de movimentação de uma transação com base na operação.
     *
     * @param operacao A operação contendo os dados necessários.
     * @return Uma String representando o tipo de movimentação (conforme enum TipoMovimentacao).
     */
    public String determinarTipoMovimentacao(Operacao operacao) {
        log.info("Iniciando determinação do tipo de movimentação para Operação: {}", operacao);

        String entradaSaida = operacao.getEntradaSaida();
        String movimentacao = operacao.getMovimentacao().toLowerCase().trim(); // Normaliza a movimentação

        log.debug("EntradaSaida: {}, Movimentacao: {}", entradaSaida, movimentacao);

        // Mapear movimentações para cada tipo de Crédito/Débito
        Set<String> creditosEntrada = Set.of(
                "rendimento", "pagamento juros", "pagamento de juros", "dividendo", "juros sobre capital próprio");
        Set<String> debitosEntrada = Set.of(
                "transferência - liquidação", "compra / venda a termo", "compra", "compra / venda", "compra/venda definitiva a termo");
        Set<String> subscricao = Set.of(
                "direito de subscrição", "direitos de subscrição - não exercido",
                "cessão de direitos - solicitada", "cessão de direitos", "solicitação de subscrição", "recibo de subscrição",
                "direito sobras de subscrição", "direito sobras de subscrição - não exercido");

        Set<String> creditosSaida = Set.of(
                "vencimento", "transferência - liquidação", "resgate",
                "antecipação total/parcial", "compra / venda");
        Set<String> debitosSaida = Set.of(
                "cobrança de taxa semestral", "direitos de subscrição exercidos",
                "direitos de subscrição - exercido");

        Set<String> transferencias = Set.of(
                "transferência", "transferencia", "transferencia sem financeiro",
                "transferência sem financeiro",
                "rendimento - transferido", "juros sobre capital próprio - transferido");

        // Determinar Crédito/Débito baseado em entradaSaida e movimentação
        if ("entrada".equalsIgnoreCase(entradaSaida)) {
            if ("atualização".equalsIgnoreCase(movimentacao)) {
                log.info("Movimentação identificada como ATUALIZAÇÃO.");
                return TipoMovimentacao.ATUALIZACAO.name();
            } else if ("bonificação em ativos".equalsIgnoreCase(movimentacao)) {
                log.info("Movimentação identificada como BONIFICAÇÃO_EM_ATIVOS.");
                return TipoMovimentacao.BONIFICACAO_EM_ATIVOS.name();
            } else if ("amortização".equalsIgnoreCase(movimentacao)) {
                log.info("Movimentação identificada como AMORTIZACAO.");
                return TipoMovimentacao.AMORTIZACAO.name();
            } else if (creditosEntrada.contains(movimentacao)) {
                log.info("Movimentação identificada como CRÉDITO (entrada).");
                return TipoMovimentacao.CREDITO.name();
            } else if (debitosEntrada.contains(movimentacao)) {
                log.info("Movimentação identificada como DÉBITO (entrada).");
                return TipoMovimentacao.DEBITO.name();
            } else if (subscricao.contains(movimentacao)) {
                log.info("Movimentação identificada como SUBSCRICAO (entrada).");
                return TipoMovimentacao.SUBSCRICAO.name();
            } else if (transferencias.contains(movimentacao)) {
                log.info("Movimentação identificada como TRANSFERENCIA (entrada).");
                return TipoMovimentacao.TRANSFERENCIA.name();
            }
        } else if ("saída".equalsIgnoreCase(entradaSaida)) {
            if (creditosSaida.contains(movimentacao)) {
                log.info("Movimentação identificada como CRÉDITO (saída).");
                return TipoMovimentacao.CREDITO.name();
            } else if (debitosSaida.contains(movimentacao)) {
                log.info("Movimentação identificada como DÉBITO (saída).");
                return TipoMovimentacao.DEBITO.name();
            } else if (subscricao.contains(movimentacao)) {
                log.info("Movimentação identificada como SUBSCRICAO (saída).");
                return TipoMovimentacao.SUBSCRICAO.name();
            } else if (transferencias.contains(movimentacao)) {
                log.info("Movimentação identificada como TRANSFERENCIA (saída).");
                return TipoMovimentacao.TRANSFERENCIA.name();
            }
        } else {
            log.warn("Movimentação não classificada: entradaSaida={}, movimentacao={}", entradaSaida, movimentacao);
        }

        return "";
    }
}
