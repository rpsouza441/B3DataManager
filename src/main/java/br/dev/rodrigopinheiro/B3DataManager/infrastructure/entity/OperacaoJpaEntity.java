package br.dev.rodrigopinheiro.B3DataManager.infrastructure.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade JPA que representa uma operação na camada de infraestrutura.
 * Espelho da entidade de domínio para persistência.
 */
@Entity
@Table(name = "operacao")
public class OperacaoJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "entrada_saida", nullable = false)
    private String entradaSaida;
    
    @Column(name = "data", nullable = false)
    private LocalDate data;
    
    @Column(name = "movimentacao", nullable = false)
    private String movimentacao;
    
    @Column(name = "produto", nullable = false)
    private String produto;
    
    @Column(name = "instituicao", nullable = false)
    private String instituicao;
    
    @Column(name = "quantidade", nullable = false)
    private double quantidade; // Mantém double para compatibilidade com DB existente
    
    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;
    
    @Column(name = "valor_operacao", nullable = false)
    private BigDecimal valorOperacao;
    
    @Column(name = "duplicado", nullable = false)
    private Boolean duplicado = false;
    
    @Column(name = "dimensionado", nullable = false)
    private Boolean dimensionado = false;
    
    @Column(name = "id_original", nullable = true)
    private Long idOriginal;
    
    @Column(name = "deletado", nullable = false)
    private Boolean deletado = false;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    // Construtores
    public OperacaoJpaEntity() {}
    
    public OperacaoJpaEntity(String entradaSaida, LocalDate data, String movimentacao,
                            String produto, String instituicao, double quantidade,
                            BigDecimal precoUnitario, BigDecimal valorOperacao,
                            Boolean duplicado, Boolean dimensionado, Long idOriginal,
                            Boolean deletado, Long usuarioId) {
        this.entradaSaida = entradaSaida;
        this.data = data;
        this.movimentacao = movimentacao;
        this.produto = produto;
        this.instituicao = instituicao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorOperacao = valorOperacao;
        this.duplicado = duplicado;
        this.dimensionado = dimensionado;
        this.idOriginal = idOriginal;
        this.deletado = deletado;
        this.usuarioId = usuarioId;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEntradaSaida() {
        return entradaSaida;
    }
    
    public void setEntradaSaida(String entradaSaida) {
        this.entradaSaida = entradaSaida;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public String getMovimentacao() {
        return movimentacao;
    }
    
    public void setMovimentacao(String movimentacao) {
        this.movimentacao = movimentacao;
    }
    
    public String getProduto() {
        return produto;
    }
    
    public void setProduto(String produto) {
        this.produto = produto;
    }
    
    public String getInstituicao() {
        return instituicao;
    }
    
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }
    
    public double getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
    
    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
    
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
    
    public BigDecimal getValorOperacao() {
        return valorOperacao;
    }
    
    public void setValorOperacao(BigDecimal valorOperacao) {
        this.valorOperacao = valorOperacao;
    }
    
    public Boolean getDuplicado() {
        return duplicado;
    }
    
    public void setDuplicado(Boolean duplicado) {
        this.duplicado = duplicado;
    }
    
    public Boolean getDimensionado() {
        return dimensionado;
    }
    
    public void setDimensionado(Boolean dimensionado) {
        this.dimensionado = dimensionado;
    }
    
    public Long getIdOriginal() {
        return idOriginal;
    }
    
    public void setIdOriginal(Long idOriginal) {
        this.idOriginal = idOriginal;
    }
    
    public Boolean getDeletado() {
        return deletado;
    }
    
    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperacaoJpaEntity that = (OperacaoJpaEntity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "OperacaoJpaEntity{" +
                "id=" + id +
                ", produto='" + produto + '\'' +
                ", data=" + data +
                ", quantidade=" + quantidade +
                ", valorOperacao=" + valorOperacao +
                ", usuarioId=" + usuarioId +
                '}';
    }
}