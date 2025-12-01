# Análise: Value Objects Dinheiro e Quantidade

**Data**: 2025-12-01  
**Status**: Classes já implementadas e em uso  
**Localização**: `domain/valueobject/`

## Status Atual

### Implementação Existente

#### Dinheiro.java
- Classe final com validações
- Garante valores não negativos
- Precisão fixa de 2 casas decimais
- Operações: add, subtract, multiply
- Imutável (sempre retorna novo objeto)

#### Quantidade.java
- Record Java (imutável por design)
- Garante valores não negativos
- Precisão de 8 casas decimais
- Operações: add, subtract, multiply

### Uso Atual

**Onde está sendo usado**:
- `Operacao.java` (domain model) - USO COMPLETO ✅
  - `quantidade: Quantidade`
  - `precoUnitario: Dinheiro`
  - `valorOperacao: Dinheiro`
  - `valorCalculado: Dinheiro`

**Onde NÃO está sendo usado**:
- `AtivoFinanceiroEntity` - usa BigDecimal
- `TransacaoEntity` - usa BigDecimal
- `PosicaoEntity` - usa BigDecimal
- Todos os DTOs - usam BigDecimal

## Análise Custo-Benefício

### Benefícios ✅

#### 1. Type Safety
```java
// SEM Value Objects
public void calcular(BigDecimal valor1, BigDecimal valor2) {
    // Qual é dinheiro? Qual é quantidade? 
    // Pode confundir na chamada!
}

// COM Value Objects
public void calcular(Dinheiro preco, Quantidade qtd) {
    // Impossível confundir os tipos!
}
```

#### 2. Validações Centralizadas
```java
// SEM Value Objects
if (preco.compareTo(BigDecimal.ZERO) < 0) {
    throw new Exception("Preço negativo");
}
// Repetir em TODOS os lugares...

// COM Value Objects
new Dinheiro(preco); // Valida automaticamente!
```

#### 3. Expressividade do Domínio
```java
// SEM
BigDecimal total = qtd.multiply(preco);

// COM
Dinheiro total = quantidade.multiply(preco.getValue());
```

#### 4. Imutabilidade Garantida
```java
Dinheiro preco = new Dinheiro(100);
preco.add(new Dinheiro(50)); // Retorna NOVO objeto
// preco ainda é 100 (imutável)
```

### Desvantagens ❌

#### 1. Conversão Constante
```java
// Entity usa BigDecimal, Domain usa Dinheiro
// Mapper precisa converter sempre:
entity.setPreco(dinheiro.getValue());
Dinheiro preco = new Dinheiro(entity.getPreco());
```

#### 2. Complexidade nos Mappers
```java
// OperacaoMapper (já implementado)
public OperacaoEntity toEntity(Operacao operacao) {
    entity.setQuantidade(operacao.getQuantidade().value());
    entity.setPrecoUnitario(operacao.getPrecoUnitario().getValue());
    entity.setValorOperacao(operacao.getValorOperacao().getValue());
    entity.setValorCalculado(operacao.getValorCalculado().getValue());
}
```

#### 3. Compatibilidade JPA
- Entities precisam usar BigDecimal (JPA não entende Value Objects nativamente)
- Precisa de AttributeConverter para cada Value Object (complexo)
- Ou fazer conversão manual nos mappers (já feito)

#### 4. Overhead em Operações de Massa
```java
// Para cada operação importada:
new Quantidade(qtd);        // Validação + conversão
new Dinheiro(preco);        // Validação + conversão
new Dinheiro(valor);        // Validação + conversão
// Multiplicado por 1000+ operações = overhead
```

## Recomendação

### Manter Uso Atual (Híbrido) ✅

**Domain Layer**: Use Value Objects
- `Operacao.java` - MANTER como está ✅
- Futuros domain models - Considerar uso

**Infrastructure Layer**: Use BigDecimal
- Entities JPA - MANTER BigDecimal
- Repositories - MANTER BigDecimal

**Application Layer**: Flexível
- Commands - BigDecimal (mais simples)
- Results/DTOs - BigDecimal (mais simples)

### NÃO Expandir para Entities

**Razões**:
1. JPA trabalha nativamente com BigDecimal
2. Performance em bulk operations
3. Queries SQL precisam de BigDecimal
4. Compatibilidade com bibliotecas externas

### Justificativa

```
┌─────────────────────────────────────────────────────────────┐
│                    ARQUITETURA ATUAL                        │
├─────────────────────────────────────────────────────────────┤
│  PRESENTATION LAYER                                         │
│  └── DTOs (BigDecimal) ← Simples para JSON/Vaadin          │
│                                                             │
│  APPLICATION LAYER                                          │
│  └── Commands/Results (BigDecimal) ← Pragmático            │
│                                                             │
│  DOMAIN LAYER                                               │
│  └── Models (Value Objects) ← Type Safety + Validações ✅  │
│                                                             │
│  INFRASTRUCTURE LAYER                                       │
│  └── Entities (BigDecimal) ← JPA nativo ✅                 │
│         │                                                   │
│         └── Mappers convertem ← Camada de tradução         │
└─────────────────────────────────────────────────────────────┘
```

## Decisão Final

### ✅ MANTER Value Objects onde estão
- `Operacao.java` - Já usa, funciona bem
- Benefício real para lógica de domínio complexa
- Validações centralizadas

### ❌ NÃO EXPANDIR para:
- Entities JPA (usar BigDecimal)
- DTOs (usar BigDecimal)
- Commands simples (usar BigDecimal)

### Quando Usar Value Objects?

**SIM**, se o model domain tem:
- Lógica de negócio complexa
- Múltiplas validações
- Risco de confundir tipos primitivos
- Exemplo: `Operacao`, futuros agregados complexos

**NÃO**, se é:
- Entity JPA (infraestrutura)
- DTO (apresentação)
- Command simples (aplicação)

## Conclusão

O uso atual está **CORRETO** e **BALANCEADO**:

- Value Objects onde fazem sentido (domain models complexos)
- BigDecimal onde é pragmático (infrastructure, DTOs)
- Conversão isolada nos mappers (clean architecture)

**Não há necessidade de mudanças**.
