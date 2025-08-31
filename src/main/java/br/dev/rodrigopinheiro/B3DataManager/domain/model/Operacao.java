package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade de domínio que representa uma operação financeira.
 * Contém invariantes de negócio e lógica de validação.
 */
public class Operacao {
    
    private final Long id;
    private final String entradaSaida;
    private final LocalDate data;
    private final String movimentacao;
    private final String produto;
    private final String instituicao;
    private final Quantidade quantidade;
    private final Dinheiro precoUnitario;
    private final Dinheiro valorOperacao;
    private final Boolean duplicado;
    private final Boolean dimensionado;
    private final Long idOriginal;
    private final Boolean deletado;
    private final UsuarioId usuarioId;
    
    // Tolerância para validação de valor ≈ preço * quantidade (0.01 = 1 centavo)
    private static final BigDecimal TOLERANCIA_VALOR = new BigDecimal("0.01");
    
    public Operacao(Long id, String entradaSaida, LocalDate data, String movimentacao,
                   String produto, String instituicao, Quantidade quantidade,
                   Dinheiro precoUnitario, Dinheiro valorOperacao, Boolean duplicado,
                   Boolean dimensionado, Long idOriginal, Boolean deletado, UsuarioId usuarioId) {
        
        // Validar invariantes
        validarInvariantes(data, quantidade, precoUnitario, valorOperacao, usuarioId);
        
        // Validar regra opcional: valor ≈ preço * quantidade
        validarCoerenciaValor(quantidade, precoUnitario, valorOperacao);
        
        this.id = id;
        this.entradaSaida = entradaSaida;
        this.data = data;
        this.movimentacao = movimentacao;
        this.produto = produto;
        this.instituicao = instituicao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorOperacao = valorOperacao;
        this.duplicado = duplicado != null ? duplicado : false;
        this.dimensionado = dimensionado != null ? dimensionado : false;
        this.idOriginal = idOriginal;
        this.deletado = deletado != null ? deletado : false;
        this.usuarioId = usuarioId;
    }
    
    private void validarInvariantes(LocalDate data, Quantidade quantidade, 
                                   Dinheiro precoUnitario, Dinheiro valorOperacao, UsuarioId usuarioId) {
        
        if (data == null) {
            throw new OperacaoInvalidaException("Data da operação não pode ser nula");
        }
        
        if (quantidade == null) {
            throw new OperacaoInvalidaException("Quantidade não pode ser nula");
        }
        
        if (precoUnitario == null) {
            throw new OperacaoInvalidaException("Preço unitário não pode ser nulo");
        }
        
        if (valorOperacao == null) {
            throw new OperacaoInvalidaException("Valor da operação não pode ser nulo");
        }
        
        if (usuarioId == null) {
            throw new OperacaoInvalidaException("UsuarioId é obrigatório para toda operação");
        }
    }
    
    private void validarCoerenciaValor(Quantidade quantidade, Dinheiro precoUnitario, Dinheiro valorOperacao) {
        BigDecimal valorCalculado = quantidade.value().multiply(precoUnitario.getValue());
        BigDecimal diferenca = valorCalculado.subtract(valorOperacao.getValue()).abs();
        
        if (diferenca.compareTo(TOLERANCIA_VALOR) > 0) {
            throw new OperacaoInvalidaException(
                String.format("Valor da operação (%.2f) não confere com preço × quantidade (%.2f). Diferença: %.2f",
                    valorOperacao.getValue(), valorCalculado, diferenca)
            );
        }
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getEntradaSaida() {
        return entradaSaida;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public String getMovimentacao() {
        return movimentacao;
    }
    
    public String getProduto() {
        return produto;
    }
    
    public String getInstituicao() {
        return instituicao;
    }
    
    public Quantidade getQuantidade() {
        return quantidade;
    }
    
    public Dinheiro getPrecoUnitario() {
        return precoUnitario;
    }
    
    public Dinheiro getValorOperacao() {
        return valorOperacao;
    }
    
    public Boolean getDuplicado() {
        return duplicado;
    }
    
    public Boolean getDimensionado() {
        return dimensionado;
    }
    
    public Long getIdOriginal() {
        return idOriginal;
    }
    
    public Boolean getDeletado() {
        return deletado;
    }
    
    public UsuarioId getUsuarioId() {
        return usuarioId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operacao operacao = (Operacao) o;
        return Objects.equals(id, operacao.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Operacao{" +
                "id=" + id +
                ", produto='" + produto + '\'' +
                ", data=" + data +
                ", quantidade=" + quantidade +
                ", valorOperacao=" + valorOperacao +
                ", usuarioId=" + usuarioId +
                '}';
    }
}