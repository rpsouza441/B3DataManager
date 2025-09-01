package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade JPA que representa uma operação consolidada.
 * 
 * <p>Esta é a representação de infraestrutura da operação,
 * mapeada para a tabela do banco de dados.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "operacao")
public class OperacaoEntity {

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
    private double quantidade;

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

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude // Evita loops
    private br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario usuario;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OperacaoEntity that = (OperacaoEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}