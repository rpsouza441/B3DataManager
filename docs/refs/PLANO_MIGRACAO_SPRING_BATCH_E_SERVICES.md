# Plano de Migração: Spring Batch e Services para Use Cases

## Status do Projeto

**Última Atualização**: 2025-12-01

### Completado
- Módulo de Import (hexagonal completo)
- Módulo de Operação (hexagonal completo)
- Views de apresentação de operações funcionando

### Em Andamento
- Spring Batch (estrutura criada, falta lógica)
- Migração de Services para Use Cases

### Próximos Passos
1. Completar Spring Batch para operações automáticas
2. Migrar views de Renda Variável
3. Implementar cálculo de DARF
4. Criar telas de Renda Fixa

## Objetivo
Este documento detalha os passos necessários para:
1. Alinhar as configurações do Spring Batch com os princípios hexagonais e SOLID
2. Transformar Services utilizados por Views em Use Cases
3. Identificar e remover Services/Use Cases não utilizados

## Roadmap Incremental (Agile)

### Sprint 1-2: Spring Batch (2 semanas)
Foco: Automatizar processamento de operações

### Sprint 3-4: Renda Variável (2 semanas)
Foco: Migrar views de Ações e FIIs

### Sprint 5-6: Sistema DARF (2 semanas)
Foco: Cálculo automático de impostos

### Sprint 7: Limpeza (1 semana)
Foco: Remover código não utilizado

### Sprint 8-9: Renda Fixa (2 semanas)
Foco: Telas de renda fixa

---

## 1. MIGRAÇÃO DO SPRING BATCH PARA ARQUITETURA HEXAGONAL

### 1.1 Problemas Identificados

#### Violações Hexagonais Atuais:
- **AtivoItemWriterConfig**: Usa diretamente `AtivoFinanceiroRepository` (infraestrutura) na camada de aplicação
- **BatchService**: Usa diretamente `JobLauncher` e `Job` (Spring Batch) na camada de aplicação
- **OperacaoItemProcessor**: Possui TODO para implementar lógica usando Use Case apropriado

#### Violações SOLID:
- **Dependency Inversion Principle (DIP)**: Dependência direta de abstrações de infraestrutura
- **Single Responsibility Principle (SRP)**: Configurações misturadas com lógica de negócio

### 1.2 Plano de Refatoração

#### Passo 1: Criar Use Cases para Spring Batch
```
📁 application/usecase/batch/
├── ProcessAtivoFinanceiroBatchUseCase.java
├── SaveAtivoFinanceiroUseCase.java
└── GetBatchStatusUseCase.java
```

**Funcionalidades:**
- `ProcessAtivoFinanceiroBatchUseCase`: Orquestrar processamento batch
- `SaveAtivoFinanceiroUseCase`: Salvar ativos financeiros (substituir writer direto)
- `GetBatchStatusUseCase`: Consultar status do batch

#### Passo 2: Refatorar AtivoItemWriterConfig
**Antes:**
```java
@Configuration
public class AtivoItemWriterConfig {
    @Bean
    public RepositoryItemWriter<AtivoFinanceiroEntity> ativoItemWriter(
        AtivoFinanceiroRepository repository) {
        // Uso direto do repository
    }
}
```

**Depois:**
```java
@Configuration
public class AtivoItemWriterConfig {
    @Bean
    public ItemWriter<AtivoFinanceiroEntity> ativoItemWriter(
        SaveAtivoFinanceiroUseCase saveUseCase) {
        return new UseCaseItemWriter<>(saveUseCase);
    }
}
```

#### Passo 3: Criar UseCaseItemWriter
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

#### Passo 4: Refatorar BatchService
**Antes:**
```java
@Service
public class BatchService {
    private final JobLauncher jobLauncher;
    private final Job job;
    // Uso direto de componentes Spring Batch
}
```

**Depois:**
```java
@Service
public class BatchService {
    private final ProcessAtivoFinanceiroBatchUseCase processUseCase;
    private final GetBatchStatusUseCase statusUseCase;
    
    public BatchExecutionResult executeBatch(BatchCommand command) {
        return processUseCase.execute(command);
    }
}
```

#### Passo 5: Implementar OperacaoItemProcessor
**Atual:**
```java
// TODO: Implementar lógica de conversão ou usar UseCase apropriado
```

**Implementar:**
```java
@Component
public class OperacaoItemProcessor implements ItemProcessor<Operacao, ProcessedOperacao> {
    private final ProcessOperacaoUseCase processUseCase;
    
    @Override
    public ProcessedOperacao process(Operacao operacao) throws Exception {
        return processUseCase.execute(new ProcessOperacaoCommand(operacao));
    }
}
```

---

## 2. MIGRAÇÃO DE SERVICES PARA USE CASES

### 2.1 Mapeamento Services → Views

#### Services Utilizados por Views:
| Service | Views que Utilizam | Status |
|---------|-------------------|---------|
| `ErrorService` | `ImportXlsxView`, `GridwithFiltersFiiView`, `OperacaoView`, `GridwithFiltersAcoesView` | ✅ **MANTER - TRANSFORMAR EM USE CASE** |
| `UsuarioService` | `LoginView`, `RegisterView` | ✅ **MANTER - TRANSFORMAR EM USE CASE** |
| `RendaVariavelService` | `GridwithFiltersFiiView`, `GridwithFiltersAcoesView` | ✅ **MANTER - TRANSFORMAR EM USE CASE** |
| `OperacaoFormatterService` | `OperacaoView` | ✅ **MANTER - TRANSFORMAR EM USE CASE** |
| `ThemeService` | `MainLayout` | ✅ **MANTER - TRANSFORMAR EM USE CASE** |
| `SecurityService` | `MainLayout` | ✅ **MANTER - NÃO TRANSFORMAR (Infraestrutura Spring)** |

#### Services Utilizados por Outros Services (Internamente):
| Service | Usado Por | Ação |
|---------|-----------|------|
| `InstituicaoService` | `RendaVariavelService`, `TransacaoService`, `CreateTransacaoUseCase` | ⚠️ **AVALIAR - Pode virar Use Case** |

#### Services de Domínio (Permitidos na Arquitetura Hexagonal):
| Service | Localização | Ação |
|---------|-------------|------|
| `CalculoPrecoMedioService` | `domain.service` | ✅ **MANTER - Domain Service** |
| `TipoAtivoVariavelService` | `domain.service` | ✅ **MANTER - Domain Service** |

#### Services de Infraestrutura:
| Service | Função | Ação |
|---------|--------|------|
| `AggregatePersistenceService` | Persistência | ⚠️ **AVALIAR NECESSIDADE** |

#### Services NÃO Utilizados por Views:
| Service | Status | Ação |
|---------|---------|------|
| `AtivoFinanceiroService` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `PortfolioService` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `PortfolioSaldoService` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `RendaFixaService` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `TransacaoService` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `UploadService` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `UserCustomService` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |

### 2.2 Use Cases Existentes - Análise de Uso

#### Use Cases Utilizados por Views:
| Use Case | Views que Utilizam | Status |
|----------|-------------------|---------|
| `ProcessUploadUseCase` | `ImportXlsxView` | ✅ **MANTIDO** |
| `ListOperacoesUseCase` | `OperacaoView` | ✅ **MANTIDO** |
| `CountOperacoesUseCase` | `OperacaoView` | ✅ **MANTIDO** |

#### Use Cases Utilizados por Spring Batch:
| Use Case | Componente | Status |
|----------|------------|---------|
| `GetOperacoesForBatchUseCase` | `OperacaoItemReaderConfig`, `CustomOperacaoItemReader` | ✅ **MANTIDO** |

#### Use Cases NÃO Utilizados por Views:
| Use Case | Status | Ação |
|----------|---------|------|
| `CheckDuplicateOperacaoUseCase` | 🚩 **USADO APENAS INTERNAMENTE** | ⚠️ **AVALIAR NECESSIDADE** |
| `GenerateErrorReportUseCase` | 🚩 **USADO APENAS INTERNAMENTE** | ⚠️ **AVALIAR NECESSIDADE** |
| `ImportExcelUseCase` | 🚩 **USADO APENAS INTERNAMENTE** | ⚠️ **AVALIAR NECESSIDADE** |
| `RegisterOperacaoUseCase` | 🚩 **USADO APENAS INTERNAMENTE** | ⚠️ **AVALIAR NECESSIDADE** |
| `CreateTransacaoUseCase` | 🚩 **USADO APENAS INTERNAMENTE** | ⚠️ **AVALIAR NECESSIDADE** |
| `CalculatePortfolioPercentagesUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `GetPortfolioUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `CreateRendaFixaUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `DeleteRendaFixaUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `GetRendaFixaUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `UpdateRendaFixaUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `CreateRendaVariavelUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `DeleteRendaVariavelUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `GetRendaVariavelUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |
| `UpdateRendaVariavelUseCase` | 🚩 **NÃO USADO POR VIEWS** | ❌ **CANDIDATO À REMOÇÃO** |

---

## 3. PLANO DE EXECUÇÃO

### Fase 1: Criação de Use Cases para Services Utilizados
**Prioridade: ALTA**

#### 3.1 ErrorService → ErrorHandlingUseCase
```java
@Component
public class ErrorHandlingUseCase {
    public void handleError(ErrorCommand command) {
        // Lógica de tratamento de erro
    }
    
    public ErrorDisplayResult formatErrorForDisplay(FormatErrorCommand command) {
        // Formatação de erro para exibição
    }
}
```

#### 3.2 UsuarioService → User Use Cases
```java
// Dividir em múltiplos use cases específicos
@Component
public class AuthenticateUserUseCase { ... }

@Component  
public class RegisterUserUseCase { ... }

@Component
public class GetUserProfileUseCase { ... }
```

#### 3.3 RendaVariavelService → RendaVariavel Use Cases
```java
@Component
public class ListRendaVariavelUseCase { ... }

@Component
public class FilterRendaVariavelUseCase { ... }

@Component
public class GetRendaVariavelDetailsUseCase { ... }
```

#### 3.4 OperacaoFormatterService → FormatOperacaoUseCase
```java
@Component
public class FormatOperacaoUseCase {
    public FormattedOperacaoResult format(FormatOperacaoCommand command) {
        // Lógica de formatação
    }
}
```

#### 3.5 ThemeService → ThemeManagementUseCase
```java
@Component
public class ThemeManagementUseCase {
    public void changeTheme(ChangeThemeCommand command) {
        // Lógica de mudança de tema
    }
    
    public ThemeResult getCurrentTheme() {
        // Obter tema atual
    }
}
```

### Fase 2: Atualização das Views
**Prioridade: ALTA**

Substituir injeções de Services por Use Cases em:
- `MainLayout.java`
- `ImportXlsxView.java` 
- `GridwithFiltersFiiView.java`
- `LoginView.java`
- `RegisterView.java`
- `OperacaoView.java`
- `GridwithFiltersAcoesView.java`

### Fase 3: Remoção de Services Não Utilizados
**Prioridade: MÉDIA**

#### Services para Remoção:
- ❌ `AtivoFinanceiroService.java`
- ❌ `PortfolioService.java`
- ❌ `PortfolioSaldoService.java`
- ❌ `RendaFixaService.java`
- ❌ `TransacaoService.java`
- ❌ `UploadService.java`
- ❌ `UserCustomService.java`

### Fase 4: Remoção de Use Cases Não Utilizados
**Prioridade: BAIXA**

#### Use Cases para Remoção:
- ❌ `CalculatePortfolioPercentagesUseCase.java`
- ❌ `GetPortfolioUseCase.java`
- ❌ `CreateRendaFixaUseCase.java`
- ❌ `DeleteRendaFixaUseCase.java`
- ❌ `GetRendaFixaUseCase.java`
- ❌ `UpdateRendaFixaUseCase.java`
- ❌ `CreateRendaVariavelUseCase.java`
- ❌ `DeleteRendaVariavelUseCase.java`
- ❌ `GetRendaVariavelUseCase.java`
- ❌ `UpdateRendaVariavelUseCase.java`

### Fase 5: Migração Spring Batch
**Prioridade: ALTA**

1. Criar Use Cases para batch
2. Implementar `UseCaseItemWriter`
3. Refatorar configurações
4. Implementar `OperacaoItemProcessor`
5. Atualizar `BatchService`

---

## 4. CHECKLIST DE VALIDAÇÃO

### ✅ Spring Batch
- [ ] ❌ `AtivoItemWriterConfig` usa Use Case em vez de Repository
- [ ] ❌ `BatchService` usa Use Cases em vez de componentes Spring Batch diretos
- [ ] ⚠️ `OperacaoItemProcessor` implementado com Use Case (ESTRUTURA CRIADA, FALTA LÓGICA)
- [ ] ❌ Criados Use Cases específicos para batch

### ✅ Services → Use Cases
- [ ] ❌ `ErrorService` → `ErrorHandlingUseCase`
- [ ] ❌ `UsuarioService` → User Use Cases
- [ ] ❌ `RendaVariavelService` → RendaVariavel Use Cases  
- [ ] ❌ `OperacaoFormatterService` → `FormatOperacaoUseCase`
- [ ] ❌ `ThemeService` → `ThemeManagementUseCase`
- [ ] ⚠️ `InstituicaoService` → Avaliar se deve virar Use Case

### ✅ Views Atualizadas
- [ ] ❌ `MainLayout.java` (usa `SecurityService`, `ThemeService`)
- [ ] ⚠️ `ImportXlsxView.java` (PARCIAL: usa `ErrorService` + `ProcessUploadUseCase`)
- [ ] ❌ `GridwithFiltersFiiView.java` (usa `ErrorService`, `RendaVariavelService`)
- [ ] ❌ `LoginView.java` (usa `UsuarioService`)
- [ ] ❌ `RegisterView.java` (usa `UsuarioService`)
- [ ] ⚠️ `OperacaoView.java` (PARCIAL: usa 2 Services + 2 Use Cases)
- [ ] ❌ `GridwithFiltersAcoesView.java` (usa `ErrorService`, `RendaVariavelService`)

### ✅ Limpeza
- [ ] ❌ Services não utilizados removidos
- [ ] ❌ Use Cases não utilizados removidos
- [ ] ❌ Imports desnecessários removidos
- [ ] ❌ Testes atualizados

---

## 5. OBSERVAÇÕES IMPORTANTES

### 🚨 Atenção Especial
- **TransacaoService**: Possui método depreciado que referencia `CreateTransacaoUseCase` - verificar se pode ser removido
- **Use Cases Internos**: Alguns use cases são utilizados apenas internamente por outros use cases - avaliar se devem ser mantidos

### 📋 Próximos Passos Recomendados
1. Implementar Use Cases para Services utilizados por Views
2. Atualizar Views para usar Use Cases
3. Migrar configurações Spring Batch
4. Remover componentes não utilizados
5. Executar testes de regressão

### 🎯 Resultado Esperado
- ✅ 100% aderência aos princípios hexagonais
- ✅ Separação clara entre camadas
- ✅ Use Cases específicos e focados
- ✅ Código mais limpo e manutenível
- ✅ Remoção de código morto

---

## Guia de Retomada de Trabalho

### Quando Retomar o Projeto

#### 1. Verifique o Status Atual
```bash
# Ver o que está em andamento
git status
git log -5 --oneline
```

#### 2. Consulte a Task List
Abra `task.md` na pasta `.gemini/antigravity/brain/cd75268b-fee1-40b3-a301-b08c900ae25e/`

#### 3. Execute os Testes
```bash
mvn test
```

#### 4. Próxima Tarefa a Fazer
Baseado no checklist, o próximo passo é:
- Se Spring Batch não está completo: Seção 1.2 (Refatoração Spring Batch)
- Se Spring Batch completo: Fase 1 (Criação de Use Cases)
- Sempre trabalhe em incrementos pequenos (1-2 horas de trabalho)

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

Use este documento como referência técnica.
Use `task.md` para tracking diário.
Mantenha commits pequenos e frequentes.

### Prioridades por Objetivo

**Objetivo: Spring Batch Funcionando**
1. Passo 1: Criar Use Cases (Dia 1-2)
2. Passo 2-3: Refatorar configurações (Dia 3-4)
3. Passo 4: Implementar processor (Dia 5)
4. Passo 5: Atualizar service (Dia 6)
5. Testes (Dia 7)

**Objetivo: Telas Renda Variável**
1. Migrar RendaVariavelService (Dia 1-3)
2. Atualizar GridwithFiltersAcoesView (Dia 4-5)
3. Atualizar GridwithFiltersFiiView (Dia 6-7)
4. Testes (Dia 8)

**Objetivo: Cálculo DARF**
1. Criar domain models (Dia 1-2)
2. Implementar use cases (Dia 3-5)
3. Criar view (Dia 6-8)
4. Job agendado (Dia 9-10)