package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "renda_variavel")
public class RendaVariavelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "data_compra")
    private LocalDate dataCompra;

    @Column(name = "preco_unitario")
    private BigDecimal precoUnitario;

    @Column(name = "quantidade")
    private double quantidade;

    @Column(name = "total")
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "ativo_financeiro_id")
    private AtivoFinanceiroEntity ativoFinanceiro;

    @Column(name = "tipo_renda_variavel", nullable = false)
    private String tipoRendaVariavel;

    public void setTipoRendaVariavel(TipoAtivoFinanceiroVariavel tipoRendaVariavel) {
        this.tipoRendaVariavel = tipoRendaVariavel.name();
    }

    public TipoAtivoFinanceiroVariavel getTipoRendaVariavel() {
        return this.tipoRendaVariavel != null ? TipoAtivoFinanceiroVariavel.valueOf(this.tipoRendaVariavel) : null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RendaVariavelEntity that = (RendaVariavelEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
