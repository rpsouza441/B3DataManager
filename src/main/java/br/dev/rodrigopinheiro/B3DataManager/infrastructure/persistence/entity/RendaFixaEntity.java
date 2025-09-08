package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
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
@Table(name = "renda_fixa")
public class RendaFixaEntity {

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

    @Column(name = "tipo_renda_fixa", nullable = false)
    private String tipoRendaFixa;

    public void setTipoRendaFixa(TipoAtivoFinanceiroFixa tipoRendaFixa) {
        this.tipoRendaFixa = tipoRendaFixa.name();
    }

    public TipoAtivoFinanceiroFixa getTipoRendaFixa() {
        return this.tipoRendaFixa != null ? TipoAtivoFinanceiroFixa.valueOf(this.tipoRendaFixa) : null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RendaFixaEntity rendaFixa = (RendaFixaEntity) o;
        return getId() != null && Objects.equals(getId(), rendaFixa.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}