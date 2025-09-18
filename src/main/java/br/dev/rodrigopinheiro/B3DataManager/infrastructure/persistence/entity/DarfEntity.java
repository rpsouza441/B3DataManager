package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.StatusDarf;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "darf")
public class DarfEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "esta_pago")
    private boolean estaPago;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;



    // ✅ ADICIONAR: Relacionamento com usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "usuario_id", insertable = false, updatable = false)
    private Long usuarioId;

    // ✅ ADICIONAR: Período de referência
    @Column(name = "mes_referencia", nullable = false)
    private Integer mesReferencia;

    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    // ✅ ADICIONAR: Status do DARF
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusDarf status; // PENDENTE, PAGO, VENCIDO

    // ✅ ADICIONAR: Data de vencimento
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @OneToMany(mappedBy = "darf")
    @ToString.Exclude // Evita loops
    private List<TransacaoEntity> transacoes;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DarfEntity darf = (DarfEntity) o;
        return getId() != null && Objects.equals(getId(), darf.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}