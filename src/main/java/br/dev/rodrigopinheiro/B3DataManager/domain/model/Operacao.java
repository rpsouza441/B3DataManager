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
    private final Dinheiro valorOperacao;  // Valor original da B3
    private final Dinheiro valorCalculado; // Valor calculado (quantidade × preço)
    private final Boolean duplicado;
    private final Boolean dimensionado;
    private final Long idOriginal;
    private final Boolean deletado;
    private final UsuarioId usuarioId;
    
    // Constante removida: TOLERANCIA_VALOR
    // Não é mais necessária pois removemos a validação de coerência de valor
    
    public Operacao(Long id, String entradaSaida, LocalDate data, String movimentacao,
                   String produto, String instituicao, Quantidade quantidade,
                   Dinheiro precoUnitario, Dinheiro valorOperacao, Boolean duplicado,
                   Boolean dimensionado, Long idOriginal, Boolean deletado, UsuarioId usuarioId) {
        
        // Validar invariantes
        validarInvariantes(data, quantidade, precoUnitario, valorOperacao, usuarioId);
        
        // Nota: Removida validação de coerência de valor pois as empresas arredondam
        // para baixo propositalmente. Agora temos colunas separadas para mostrar
        // tanto o valor original da B3 quanto o valor calculado.
        
        this.id = id;
        this.entradaSaida = entradaSaida;
        this.data = data;
        this.movimentacao = movimentacao;
        this.produto = produto;
        this.instituicao = instituicao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorOperacao = valorOperacao;  // Valor original da B3
        this.valorCalculado = calcularValorCorreto(quantidade, precoUnitario); // Valor calculado
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
    
    /**
     * Calcula o valor correto da operação (quantidade × preço unitário).
     */
    private Dinheiro calcularValorCorreto(Quantidade quantidade, Dinheiro precoUnitario) {
        // Para operações sem valor (direitos, atualizações, etc.), manter zero
        if (quantidade.value().compareTo(BigDecimal.ZERO) == 0 ||
             precoUnitario.getValue().compareTo(BigDecimal.ZERO) == 0) {
             return new Dinheiro(BigDecimal.ZERO);
         }
        
        BigDecimal valorCalculado = quantidade.value().multiply(precoUnitario.getValue());
        return new Dinheiro(valorCalculado);
    }

    // Método removido: validarCoerenciaValor
    // Razão: As empresas arredondam valores propositalmente para baixo.
    // Agora temos colunas separadas para mostrar valor B3 vs valor calculado.
    
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

    /**
     * Retorna o valor calculado corretamente (quantidade × preço unitário).
     * Este é o valor que deveria estar correto matematicamente.
     */
    public Dinheiro getValorCalculado() {
        return valorCalculado;
    }

    /**
     * Verifica se há diferença entre o valor da B3 e o valor calculado.
     */
    public boolean temDiferencaValor() {
        if (quantidade.value().compareTo(BigDecimal.ZERO) == 0 || 
            precoUnitario.getValue().compareTo(BigDecimal.ZERO) == 0) {
            return false; // Operações sem valor não têm diferença
        }
        
        BigDecimal diferenca = valorCalculado.getValue().subtract(valorOperacao.getValue()).abs();
        return diferenca.compareTo(new BigDecimal("0.01")) > 0;
    }

    /**
     * Retorna a diferença entre valor calculado e valor da B3.
     */
    public BigDecimal getDiferencaValor() {
        return valorCalculado.getValue().subtract(valorOperacao.getValue()).abs();
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