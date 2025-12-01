# B3DataManager - Plano de Trabalho

**Última Atualização**: 2025-12-01  
**Objetivo**: Completar migração hexagonal, Spring Batch, telas e cálculo de DARF

## Status Atual

### Completado
- ✅ Módulo Import (hexagonal)
- ✅ Módulo Operação (hexagonal)
- ✅ Estratégia SINGLE_TABLE para AtivoFinanceiro
- ✅ Entities: AtivoFinanceiroEntity, AtivoRendaVariavelEntity, AtivoRendaFixaEntity
- ✅ Views de apresentação de operações

### Em Andamento
- 🔄 Spring Batch (estrutura existe, falta implementação)
- 🔄 Migração de Services para Use Cases

### Não Iniciado
- ⏸️ Telas de Renda Variável
- ⏸️ Sistema de impostos (DARF)
- ⏸️ Telas de Renda Fixa

---

## Roadmap (Sprints de 1-2 semanas)

### Sprint 1-2: Spring Batch (2 semanas)
**Objetivo**: Automatizar processamento de operações

#### Tarefas
- [ ] Criar use cases para Spring Batch
  - [ ] ProcessAtivoFinanceiroBatchUseCase
  - [ ] SaveAtivoFinanceiroUseCase
  - [ ] GetBatchStatusUseCase
- [ ] Refatorar AtivoItemWriterConfig para usar use cases
- [ ] Implementar UseCaseItemWriter genérico
- [ ] Implementar lógica em OperacaoItemProcessor
- [ ] Refatorar BatchService para usar use cases
- [ ] Testes de integração

### Sprint 3-4: Renda Variável (2 semanas)
**Objetivo**: Migrar views de Ações e FIIs

#### Tarefas
- [ ] Migrar RendaVariavelService para use cases
  - [ ] ListAcoesUseCase
  - [ ] ListFiiUseCase
  - [ ] FilterRendaVariavelUseCase
- [ ] Migrar ErrorService → ErrorHandlingUseCase
- [ ] Migrar OperacaoFormatterService → FormatOperacaoUseCase
- [ ] Atualizar GridwithFiltersAcoesView
- [ ] Atualizar GridwithFiltersFiiView
- [ ] Atualizar OperacaoView
- [ ] Testes de interface

### Sprint 5-6: Sistema DARF (2 semanas)
**Objetivo**: Cálculo automático de impostos

#### Tarefas
- [ ] Criar domain models
  - [ ] CalculoImposto
  - [ ] Darf
- [ ] Implementar use cases
  - [ ] CalculateDarfUseCase (15% sobre lucro > R$ 20k/mês)
  - [ ] GetTaxObligationsUseCase
  - [ ] CompensatePrejuizosUseCase
- [ ] Criar ImpostosView
  - [ ] Dashboard de obrigações
  - [ ] Alertas de vencimento
- [ ] Job agendado para cálculo mensal
- [ ] Testes

### Sprint 7: Limpeza (1 semana)
**Objetivo**: Remover código não utilizado

#### Services para Remover
- [ ] AtivoFinanceiroService
- [ ] PortfolioService
- [ ] PortfolioSaldoService
- [ ] RendaFixaService
- [ ] TransacaoService
- [ ] UploadService
- [ ] UserCustomService

#### Use Cases para Remover (não utilizados por views)
- [ ] CalculatePortfolioPercentagesUseCase
- [ ] GetPortfolioUseCase
- [ ] Create/Delete/Get/UpdateRendaFixaUseCase (4 classes)
- [ ] Create/Delete/Get/UpdateRendaVariavelUseCase (4 classes)

### Sprint 8-9: Renda Fixa (2 semanas)
**Objetivo**: Telas de renda fixa

#### Tarefas
- [ ] Criar use cases para Renda Fixa
- [ ] Implementar views de Renda Fixa
- [ ] Integração com sistema de impostos
- [ ] Testes

---

## Detalhamento Técnico

### 1. Spring Batch - Arquitetura Hexagonal

#### Problema Atual
- `AtivoItemWriterConfig` usa diretamente `AtivoFinanceiroRepository` (infraestrutura)
- `BatchService` usa diretamente `JobLauncher` e `Job` (Spring Batch)
- `OperacaoItemProcessor` possui TODO para implementar lógica

#### Solução

**Use Cases Necessários**:
```
application/usecase/batch/
├── ProcessAtivoFinanceiroBatchUseCase.java
├── SaveAtivoFinanceiroUseCase.java
└── GetBatchStatusUseCase.java
```

**Refatoração AtivoItemWriterConfig**:
```java
// ANTES
@Bean
public RepositoryItemWriter<AtivoFinanceiroEntity> ativoItemWriter(
    AtivoFinanceiroRepository repository) {
    // Uso direto do repository
}

// DEPOIS
@Bean
public ItemWriter<AtivoFinanceiroEntity> ativoItemWriter(
    SaveAtivoFinanceiroUseCase saveUseCase) {
    return new UseCaseItemWriter<>(saveUseCase);
}
```

**Implementar UseCaseItemWriter**:
```java
public class UseCaseItemWriter<T> implements ItemWriter<T> {
    private final SaveAtivoFinanceiroUseCase saveUseCase;
    
    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T item : items) {
            saveUseCase.execute(createCommand(item));
        }
    }
}
```

### 2. Services → Use Cases

#### Services Utilizados por Views (MANTER e transformar)

| Service | Views que Utilizam | Use Cases Necessários |
|---------|-------------------|---------------------|
| ErrorService | ImportXlsxView, GridwithFiltersFiiView, OperacaoView, GridwithFiltersAcoesView | ErrorHandlingUseCase |
| UsuarioService | LoginView, RegisterView | AuthenticateUserUseCase, RegisterUserUseCase, GetUserProfileUseCase |
| RendaVariavelService | GridwithFiltersFiiView, GridwithFiltersAcoesView | ListRendaVariavelUseCase, FilterRendaVariavelUseCase, GetRendaVariavelDetailsUseCase |
| OperacaoFormatterService | OperacaoView | FormatOperacaoUseCase |
| ThemeService | MainLayout | ThemeManagementUseCase |

#### Services NÃO Utilizados (REMOVER)
- AtivoFinanceiroService
- PortfolioService
- PortfolioSaldoService
- RendaFixaService
- TransacaoService
- UploadService
- UserCustomService

### 3. Sistema de Impostos (DARF)

#### Regras de Cálculo
- 15% sobre lucro > R$ 20.000/mês em operações de renda variável
- Compensação de prejuízos
- Vencimento: último dia útil do mês seguinte
- Isenção para vendas < R$ 20k/mês

#### Arquitetura
```
domain/model/
├── CalculoImposto.java
└── Darf.java

application/usecase/imposto/
├── CalculateDarfUseCase.java
├── GetTaxObligationsUseCase.java
└── CompensatePrejuizosUseCase.java

presentation/view/
└── ImpostosView.java
```

---

## Infraestrutura Pendente

### Flyway (Prioridade: Média)
- [ ] Adicionar dependências flyway-core e flyway-mysql
- [ ] Criar migração baseline V1__baseline.sql
- [ ] Configurar spring.flyway.enabled=true

### Actuator (Prioridade: Média)
- [ ] Habilitar endpoints essenciais (health, metrics, info)
- [ ] Configurar segurança dos endpoints

### Segurança (Prioridade: Baixa)
- [ ] Corrigir Frame Options (SAMEORIGIN)
- [ ] Implementar Rate Limiting (Resilience4j)

### API de Preços (Futuro)
- [ ] Interface MarketDataProvider
- [ ] Implementação BRAPI
- [ ] Circuit Breaker
- [ ] Cache de preços

---

## Guia de Retomada

### Quando Voltar ao Projeto

1. **Verificar Status**
```bash
git status
git log -5 --oneline
```

2. **Consultar Task List**
Abra `task.md` em `.gemini/antigravity/brain/cd75268b-fee1-40b3-a301-b08c900ae25e/`

3. **Executar Testes**
```bash
mvn test
```

4. **Próxima Tarefa**
- Se Spring Batch não completo: Criar use cases (Sprint 1-2)
- Se Spring Batch completo: Migrar services (Sprint 3-4)
- Sempre trabalhe em incrementos de 1-2 horas

### Estratégia de Trabalho Incremental

#### Sessões de 1-2 Horas
1. Escolha UMA tarefa do checklist
2. Implemente com testes
3. Commit com mensagem clara
4. Atualize o checklist

#### Exemplo de Sessão
```
Sessão 1: Criar ProcessAtivoFinanceiroBatchUseCase
- Criar interface do use case
- Implementar use case
- Escrever testes
- Commit: "feat: add ProcessAtivoFinanceiroBatchUseCase"
- Atualizar checklist
```

### Tracking de Progresso
- Use este documento como referência técnica
- Use `task.md` para tracking diário
- Mantenha commits pequenos e frequentes

---

## Backlog Futuro
- [ ] Dashboard consolidado
- [ ] Sistema de análise de investimentos
- [ ] API REST pública
- [ ] Mobile app
- [ ] Dockerização
