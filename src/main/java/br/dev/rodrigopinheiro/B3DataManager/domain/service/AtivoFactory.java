package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Transacao;

/**
 * Interface para a criação de ativos financeiros.
 *
 * <p>
 * Esta fábrica é responsável por orquestrar a criação de um objeto {@link AtivoFinanceiro}
 * a partir dos dados contidos em uma {@link Operacao} e na respectiva {@link Transacao}.
 * O processo inclui a criação e associação da renda (fixa ou variável) ao ativo,
 * utilizando internamente a {@code RendaFactory}.
 * </p>
 *
 * <p>
 * Ao definir uma interface, facilitamos a manutenção, a testabilidade e a possibilidade de
 * fornecer diferentes implementações sem que os consumidores precisem conhecer os detalhes da criação.
 * </p>
 */
public interface AtivoFactory {

    /**
     * Cria e configura um ativo financeiro com base nos dados da operação e da transação.
     *
     * <p>
     * Este método é responsável por:
     * <ul>
     *   <li>Extrair o ticker do produto informado na operação;</li>
     *   <li>Buscar ou criar o {@code AtivoFinanceiro} correspondente (utilizando, por exemplo, um serviço de ativo);</li>
     *   <li>Criar a {@code Renda} associada ao ativo (renda fixa ou variável) por meio da {@code RendaFactory};</li>
     *   <li>Associar a renda e a transação ao ativo.</li>
     * </ul>
     * </p>
     *
     * @param operacao  A operação que contém os dados de entrada para a criação do ativo.
     * @param portfolio para a vinculacao com ativo ou a busca dele se ja existir.
     * @return Um objeto {@link AtivoFinanceiro} devidamente criado e configurado.
     */
    AtivoFinanceiro criarAtivo(Operacao operacao, Portfolio portfolio);
}
