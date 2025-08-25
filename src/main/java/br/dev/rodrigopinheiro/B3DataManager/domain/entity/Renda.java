package br.dev.rodrigopinheiro.B3DataManager.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "tipo_renda", discriminatorType = DiscriminatorType.STRING)
public abstract class Renda {

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
    private AtivoFinanceiro ativoFinanceiro;

}
