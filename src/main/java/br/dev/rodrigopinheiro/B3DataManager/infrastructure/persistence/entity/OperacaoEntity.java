package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade JPA que representa uma operação financeira na camada de infraestrutura.
 * 
 * Centraliza todas as informações relacionadas a operações de mercado, incluindo:
 * - Dados da operação (entrada/saída, data, movimentação)
 * - Informações do produto e instituição
 * - Valores financeiros (quantidade, preço, valor total)
 * - Controles de processamento e duplicação
 * - Relacionamento com usuário proprietário
 * 
 * Características:
 * - Espelho da entidade de domínio para persistência
 * - Auditoria automática via AuditableEntity
 * - Soft delete para manter integridade
 * - Controle de duplicação e processamento
 * - Relacionamento lazy com usuário
 * - Suporte a operações de importação em lote
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "operacao")
public class OperacaoEntity extends AuditableEntity {
    
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
    
    @Column(name = "processado", nullable = false)
    private Boolean processado = false;
    
    @Column(name = "id_original", nullable = true)
    private Long idOriginal;
    
    @Column(name = "deletado", nullable = false)
    private Boolean deletado = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
    
    // Campo para facilitar consultas sem carregar a entidade completa
    @Column(name = "usuario_id", insertable = false, updatable = false)
    private Long usuarioId;
    
    
    public OperacaoEntity(String entradaSaida, LocalDate data, String movimentacao,
                          String produto, String instituicao, double quantidade,
                          BigDecimal precoUnitario, BigDecimal valorOperacao,
                          Boolean duplicado, Boolean processado, Long idOriginal,
                          Boolean deletado, UsuarioEntity usuario) {
        this.entradaSaida = entradaSaida;
        this.data = data;
        this.movimentacao = movimentacao;
        this.produto = produto;
        this.instituicao = instituicao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorOperacao = valorOperacao;
        this.duplicado = duplicado;
        this.processado = processado;
        this.idOriginal = idOriginal;
        this.deletado = deletado;
        this.usuario = usuario;
    }
    
    // Construtor de compatibilidade com Long usuarioId
    public OperacaoEntity(String entradaSaida, LocalDate data, String movimentacao,
                          String produto, String instituicao, double quantidade,
                          BigDecimal precoUnitario, BigDecimal valorOperacao,
                          Boolean duplicado, Boolean processado, Long idOriginal,
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
        this.processado = processado;
        this.idOriginal = idOriginal;
        this.deletado = deletado;
        this.usuarioId = usuarioId;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperacaoEntity that = (OperacaoEntity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    

}