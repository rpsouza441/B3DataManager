package br.dev.rodrigopinheiro.B3DataManager.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "portifolio")
public class Portfolio {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associação direta com o usuário
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Agrega os ativos financeiros do usuário
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<AtivoFinanceiro> ativosFinanceiro = new HashSet<>();

    // Agrega as transações realizadas no portfolio
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Transacao> transacoes = new ArrayList<>();

    @Column(name = "saldo_total")
    private BigDecimal saldoTotal;

    @Column(name = "saldo_aplicado")
    private BigDecimal saldoAplicado;

    @Column(name = "lucro_venda")
    private BigDecimal lucroVenda;

    @Column(name = "lucro_rendimento")
    private BigDecimal lucroRendimento;

    /**
     * Adiciona um ativo financeiro ao portfolio, evitando duplicações.
     * Caso o ativo já exista, a operação é ignorada e um aviso pode ser registrado.
     *
     * @param ativo O ativo financeiro a ser adicionado.
     */
    public void adicionarAtivoFinanceiro(AtivoFinanceiro ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo.");
        }
        if (this.ativosFinanceiro.contains(ativo)) {
            // Opção: ignorar a inserção ou atualizar o registro conforme a regra de negócio.
            log.info("Ativo já existe no portfolio. Operação ignorada.");
            return;
        }
        this.ativosFinanceiro.add(ativo);
        ativo.setPortfolio(this);
    }

    /**
     * Remove um ativo financeiro do portfolio e desfaz o relacionamento.
     *
     * @param ativo O ativo financeiro a ser removido.
     */
    public void removerAtivoFinanceiro(AtivoFinanceiro ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo.");
        }
        ativosFinanceiro.remove(ativo);
        ativo.setPortfolio(null);
    }

    /**
     * Adiciona uma transação ao portfolio.
     * Após a adição, atualiza os saldos de forma incremental.
     *
     * @param transacao A transação a ser adicionada.
     */
    public void adicionarTransacao(Transacao transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula.");
        }
        transacao.setPortfolio(this);
        this.transacoes.add(transacao);
        atualizarSaldos(transacao);
    }

    /**
     * Remove uma transação do portfolio e recalcula os saldos.
     *
     * @param transacao A transação a ser removida.
     */
    public void removerTransacao(Transacao transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula.");
        }
        if (this.transacoes.remove(transacao)) {
            transacao.setPortfolio(null);
            recalcularSaldos();
        }
    }

    /**
     * Atualiza os saldos do portfolio de forma incremental com base na transação informada.
     *
     * Este métod utiliza a lógica centralizada do métod privado {@code calcularImpactoTransacao}
     * para determinar o impacto da transação nos saldos do portfolio. A transação pode impactar:
     *
     * - {@code saldoTotal}: sempre acrescido do valor total da transação (exceto em transferências);
     * - {@code saldoAplicado}: acrescido do valor total quando a transação for de entrada e não for
     *   um rendimento;
     * - {@code lucroVenda}: acrescido do valor resultante da subtração entre o valor total e o custo total
     *   (preço médio multiplicado pela quantidade) para transações de saída;
     * - {@code lucroRendimento}: acrescido do valor total da transação quando esta representar um rendimento
     *   (por exemplo, LUCRO_RENDIMENTO, LUCRO_DIVIDENDO, LUCRO_JUROS ou LUCRO_OUTRA) em entradas.
     *
     * Caso a transação seja do tipo "TRANSFERENCIA" ou seu valor total seja nulo, nenhum saldo será alterado.
     *
     * @param transacao a transação utilizada para atualizar os saldos do portfolio.
     * @throws IllegalArgumentException se o valor total da transação for nulo.
     */
    public void atualizarSaldos(Transacao transacao) {
        Map<String, BigDecimal> impacto = calcularImpactoTransacao(transacao);
        this.saldoTotal = this.saldoTotal.add(impacto.get("saldoTotal"));
        this.saldoAplicado = this.saldoAplicado.add(impacto.get("saldoAplicado"));
        this.lucroVenda = this.lucroVenda.add(impacto.get("lucroVenda"));
        this.lucroRendimento = this.lucroRendimento.add(impacto.get("lucroRendimento"));
    }


    /**
     * Recalcula todos os saldos do portfolio com base na lista completa de transações.
     *
     * Este métod itera por todas as transações associadas ao portfolio e, para cada uma, utiliza
     * o métod privado {@code calcularImpactoTransacao} para determinar o impacto nos saldos.
     * As transações do tipo "TRANSFERENCIA" são ignoradas, ou seja, não alteram nenhum saldo.
     *
     * Os saldos recalculados são:
     *
     * - {@code saldoTotal}: soma dos valores totais de todas as transações (exceto transferências);
     * - {@code saldoAplicado}: soma dos valores dos aportes (transações de entrada que não são rendimentos);
     * - {@code lucroVenda}: soma dos lucros obtidos nas transações de saída, calculados como a diferença
     *   entre o valor total e o custo total (preço médio x quantidade);
     * - {@code lucroRendimento}: soma dos valores das transações de entrada que representam rendimentos.
     *
     * Ao final, os campos do portfolio são atualizados com os valores recalculados.
     */
    public void recalcularSaldos() {
        BigDecimal novoSaldoTotal = BigDecimal.ZERO;
        BigDecimal novoSaldoAplicado = BigDecimal.ZERO;
        BigDecimal novoLucroVenda = BigDecimal.ZERO;
        BigDecimal novoLucroRendimento = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {
            Map<String, BigDecimal> impacto = calcularImpactoTransacao(transacao);
            novoSaldoTotal = novoSaldoTotal.add(impacto.get("saldoTotal"));
            novoSaldoAplicado = novoSaldoAplicado.add(impacto.get("saldoAplicado"));
            novoLucroVenda = novoLucroVenda.add(impacto.get("lucroVenda"));
            novoLucroRendimento = novoLucroRendimento.add(impacto.get("lucroRendimento"));
        }

        this.saldoTotal = novoSaldoTotal;
        this.saldoAplicado = novoSaldoAplicado;
        this.lucroVenda = novoLucroVenda;
        this.lucroRendimento = novoLucroRendimento;
    }


    /**
     * Métod privado que calcula o impacto de uma transação nos saldos do portfolio.
     * Retorna um Map com as chaves:
     *   "saldoTotal", "saldoAplicado", "lucroVenda" e "lucroRendimento".
     *
     * Se a transação for do tipo "TRANSFERENCIA" ou tiver valor nulo, retorna um Map com zeros.
     */
    private Map<String, BigDecimal> calcularImpactoTransacao(Transacao transacao) {
        // Cria e inicializa o Map com valores zerados.
        Map<String, BigDecimal> impacto = new HashMap<>();
        impacto.put("saldoTotal", BigDecimal.ZERO);
        impacto.put("saldoAplicado", BigDecimal.ZERO);
        impacto.put("lucroVenda", BigDecimal.ZERO);
        impacto.put("lucroRendimento", BigDecimal.ZERO);

        if (transacao.getValorTotal() == null) {
            return impacto;
        }
        // Se a transação for de transferência, não altera nenhum saldo.
        if ("TRANSFERENCIA".equalsIgnoreCase(transacao.getTipoMovimentacao())) {
            return impacto;
        }

        // O valor total sempre compõe o saldoTotal.
        impacto.put("saldoTotal", transacao.getValorTotal());

        if ("ENTRADA".equalsIgnoreCase(transacao.getEntradaSaida())) {
            String tipoTransacao = transacao.getTipoTransacao();
            if (tipoTransacao != null && (
                    tipoTransacao.equalsIgnoreCase("LUCRO_RENDIMENTO") ||
                            tipoTransacao.equalsIgnoreCase("LUCRO_DIVIDENDO") ||
                            tipoTransacao.equalsIgnoreCase("LUCRO_JUROS") ||
                            tipoTransacao.equalsIgnoreCase("LUCRO_OUTRA")
            )) {
                impacto.put("lucroRendimento", transacao.getValorTotal());
            } else {
                impacto.put("saldoAplicado", transacao.getValorTotal());
            }
        } else { // Transação de SAÍDA
            BigDecimal precoMedio = transacao.getPrecoMedio() != null ? transacao.getPrecoMedio() : BigDecimal.ZERO;
            BigDecimal custoTotal = precoMedio.multiply(BigDecimal.valueOf(transacao.getQuantidade()));
            BigDecimal lucro = transacao.getValorTotal().subtract(custoTotal);
            impacto.put("lucroVenda", lucro);
        }

        return impacto;
    }


    /**
     * Verifica se o portfolio contém o ativo financeiro informado.
     *
     * @param ativo O ativo financeiro a ser verificado.
     * @return true se o ativo estiver presente; false caso contrário.
     */
    public boolean possuiAtivo(AtivoFinanceiro ativo) {
        return ativo != null && this.ativosFinanceiro.contains(ativo);
    }

    /**
     * Busca os registros de Renda Fixa associados aos ativos financeiros deste portfolio.
     * Retorna uma lista permitindo registros duplicados caso o mesmo investimento tenha sido realizado mais de uma vez.
     *
     * @return Lista de registros de Renda Fixa.
     */
    public List<RendaFixa> buscarRendaFixa() {
        return ativosFinanceiro.stream()
                .filter(Objects::nonNull)
                .flatMap(ativo -> {
                    List<RendaFixa> fixas = ativo.getRendaFixas();
                    return fixas != null ? fixas.stream() : Stream.<RendaFixa>empty();
                })
                .collect(Collectors.toList());
    }

    /**
     * Busca os registros de Renda Variável associados aos ativos financeiros deste portfolio.
     * Retorna uma lista permitindo registros duplicados caso o mesmo investimento tenha sido realizado mais de uma vez.
     *
     * @return Lista de registros de Renda Variável.
     */
    public List<RendaVariavel> buscarRendaVariavel() {
        return ativosFinanceiro.stream()
                .filter(Objects::nonNull)
                .flatMap(ativo -> {
                    List<RendaVariavel> variaveis = ativo.getRendaVariaveis();
                    return variaveis != null ? variaveis.stream() : Stream.<RendaVariavel>empty();
                })
                .collect(Collectors.toList());
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Portfolio portfolio = (Portfolio) o;
        return getId() != null && Objects.equals(getId(), portfolio.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}