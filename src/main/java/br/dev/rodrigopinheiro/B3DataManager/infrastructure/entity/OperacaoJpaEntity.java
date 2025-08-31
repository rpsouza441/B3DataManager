package br.dev.rodrigopinheiro.B3DataManager.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade JPA que representa uma operação na camada de infraestrutura.
 * Espelho da entidade de domínio para persistência.
 */
@Getter
@Setter
@NoArgsConstructor
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
    
    @Column(name = "valor_calculado", precision = 15, scale = 2)
    private BigDecimal valorCalculado;
    
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