# 🏗️ Plano de Migração Hexagonal - B3DataManager

**Data de Criação:** 01/09/2025  
**Última Atualização:** 01/09/2025  
**Status:** Em Planejamento  
**Arquiteto:** Claude 4 Sonnet  

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Estado Atual](#estado-atual)
3. [Achados de Auditoria](#achados-de-auditoria)
4. [Estratégia de Migração](#estratégia-de-migração)
5. [Cronograma Detalhado](#cronograma-detalhado)
6. [Configurações Necessárias](#configurações-necessárias)
7. [Checklist de Execução](#checklist-de-execução)
8. [Riscos e Mitigações](#riscos-e-mitigações)

---

## 🎯 Visão Geral

### **Objetivo Principal**
- Migrar de DDD para **Arquitetura Hexagonal pura**
- Separar **Domain** (regras de negócio) de **Infrastructure** (persistência)
- Resolver problemas de **performance** (cálculo de % em tempo real)
- Implementar **gestão de impostos** (DARF automática)
- Criar **sistema de análise** de investimentos

### **Escopo da Migração**
- ✅ **Operacao** - JÁ MIGRADA (hexagonal)
- ✅ **Import** - JÁ MIGRADA (hexagonal)
- 🔄 **Portfolio + AtivoFinanceiro** - PRIORIDADE 1
- 🔄 **RendaVariavel + RendaFixa** - PRIORIDADE 2
- 🔄 **Transacao + Instituicao** - PRIORIDADE 3
- 🔄 **Sistema de Impostos** - PRIORIDADE 4

---

## 🔄 Controle de Versão (GitHub)

### **📋 Estratégia de Commits**

#### **🏷️ Convenção de Commits**
```
feat: adiciona nova funcionalidade
fix: corrige bug
refactor: refatoração sem mudança de funcionalidade
test: adiciona ou modifica testes
docs: atualiza documentação
chore: tarefas de manutenção
```

#### **🌿 Estratégia de Branches**
- **`main`** - Código estável e funcional
- **`develop`** - Branch de desenvolvimento
- **`feature/hexagonal-migration`** - Branch principal da migração
- **`feature/portfolio-migration`** - Migração específica do Portfolio
- **`feature/api-refactor`** - Refatoração da API externa

#### **📦 Releases Incrementais**
- **v1.1.0** - Correções críticas + Flyway
- **v1.2.0** - Portfolio + AtivoFinanceiro migrados
- **v1.3.0** - Nova API de preços
- **v1.4.0** - Sistema de impostos
- **v2.0.0** - Migração hexagonal completa

---

## 📊 Estado Atual

### **✅ Módulos Já Migrados (Hexagonal)**

#### **1. Operacao (Completa)**
- **Domain Model:** `domain.model.Operacao` (POJO puro)
- **Infrastructure:** `infrastructure.persistence.entity.OperacaoEntity` (JPA)
- **Use Cases:** `ListOperacoesUseCase`, `CountOperacoesUseCase`, `RegisterOperacaoUseCase`
- **Views:** `OperacaoView` (usando Use Cases)
- **Status:** ✅ **COMPLETO E FUNCIONAL**

#### **2. Import (Completa)**
- **Use Cases:** `ProcessUploadUseCase`, `ImportExcelUseCase`, `GenerateErrorReportUseCase`
- **Views:** `ImportXlsxView` (Vaadin 24.8+)
- **Status:** ✅ **COMPLETO E FUNCIONAL**

### **🔄 Módulos Pendentes (DDD Atual)**

#### **Entidades no domain.entity (9 entidades):**
- ❌ `AtivoFinanceiro` - **TEM VIEWS** (ações + FII) - **PRIORIDADE 1**
- ❌ `RendaVariavel` - **TEM VIEWS** (ações + FII) - **PRIORIDADE 1**
- ❌ `Portfolio` - **SEM VIEW** - **PRIORIDADE 1**
- ❌ `Transacao` - **SEM VIEW** - **PRIORIDADE 2**
- ❌ `RendaFixa` - **SEM VIEW** - **PRIORIDADE 2**
- ❌ `Instituicao` - **SEM VIEW** - **PRIORIDADE 3**
- ❌ `Usuario` - **TEM VIEW** (register/login) - **PRIORIDADE 3**
- ❌ `Renda` - **CLASSE BASE** - **PRIORIDADE 2**
- ❌ `Darf` - **SEM VIEW** - **PRIORIDADE 4**

#### **Views Existentes que Precisam Migrar:**
- 🔄 **Ações:** `GridwithFiltersAcoesView` + `FiltersAcoesView`
- 🔄 **FII:** `GridwithFiltersFiiView` + `FiltersFiiView`
- 🔄 **Usuário:** `RegisterView` + `LoginView`

---

## 🚨 Achados de Auditoria

### **📊 Resumo Executivo**
- **Total de Achados:** 10
- **Críticos:** 4 (40%)
- **Altos:** 4 (40%)
- **Médios:** 2 (20%)
- **Esforço Total:** 36.5 horas

### **🔴 CRÍTICOS - Ação Imediata**

#### **1. SEC-001: Secrets em Plain-Text**
- **Arquivo:** `application.properties:6,35`
- **Evidência:** `spring.datasource.password=nB132MUlpZ4jxn7f`
- **Impacto:** Vazamento de credenciais
- **Correção:** ⚠️ **BAIXA PRIORIDADE** - Será resolvido na dockerização
- **Justificativa:** Banco de desenvolvimento, sem dados sensíveis
- **Esforço:** Será feito com Docker Compose

#### **2. SEC-002: Frame Options Desabilitado**
- **Arquivo:** `SecurityConfig.java:44`
- **Evidência:** `frameOptions().disable()`
- **Impacto:** Vulnerabilidade a clickjacking
- **Correção:** Configurar SAMEORIGIN
- **Esforço:** 30 minutos

#### **3. OBS-001: Ausência de Actuator**
- **Arquivo:** `application.properties` (ausente)
- **Impacto:** Impossibilidade de monitoramento
- **Correção:** Habilitar Actuator com endpoints essenciais
- **Esforço:** 4 horas

#### **4. DATA-001: Ausência de Flyway**
- **Arquivo:** `pom.xml` (ausente)
- **Impacto:** Inconsistências de schema
- **Correção:** Implementar Flyway com baseline
- **Esforço:** 8 horas

### **🟠 ALTOS - Correção Prioritária**

#### **5. PERF-001: Eager Loading**
- **Arquivo:** `Usuario.java:40`
- **Evidência:** `@ElementCollection(fetch = FetchType.EAGER)`
- **Impacto:** Performance degradada
- **Correção:** Migrar para LAZY
- **Esforço:** 3 horas

#### **6. PERF-002: Open-in-View Habilitado**
- **Impacto:** Queries durante renderização
- **Correção:** Desabilitar e implementar DTOs
- **Esforço:** 6 horas

#### **7. SEC-003: Ausência de Rate Limiting**
- **Impacto:** Vulnerabilidade a DoS
- **Correção:** Implementar com Resilience4j
- **Esforço:** 4 horas

#### **8. DEP-001: Dependências Desatualizadas**
- **Evidência:** `poi:5.3.0`, `mockito-inline:5.2.0`
- **Correção:** Atualizar versões
- **Esforço:** 2 horas

---

## 🔧 Componentes a Normalizar (Hexagonal)

### **📋 Mapeamento Completo de Não-Conformidades**

#### **🔴 Services que Violam Hexagonal**

##### **1. RendaVariavelService**
- **Localização:** `application.service.RendaVariavelService`
- **Problema:** Service anêmico com lógica de domínio
- **Solução:** Migrar para Use Cases específicos
- **Use Cases Necessários:**
  - `ListAcoesUseCase`
  - `ListFiiUseCase`
  - `CalculatePerformanceUseCase`
  - `UpdateMarketPricesUseCase`

##### **2. AtivoFinanceiroService**
- **Localização:** `application.service.AtivoFinanceiroService`
- **Problema:** CRUD genérico sem regras de negócio
- **Solução:** Substituir por Use Cases específicos
- **Use Cases Necessários:**
  - `CreateAtivoUseCase`
  - `GetAtivoUseCase`
  - `UpdateAtivoUseCase`
  - `SearchAtivosUseCase`

##### **3. PortfolioService**
- **Localização:** `application.service.PortfolioService`
- **Problema:** Lógica de cálculo no service
- **Solução:** Mover cálculos para Domain Model
- **Use Cases Necessários:**
  - `GetPortfolioUseCase`
  - `CalculateDiversificationUseCase`
  - `RebalancePortfolioUseCase`

##### **4. TransacaoService**
- **Localização:** `domain.service.TransacaoService`
- **Problema:** Service no domain (deveria ser Use Case)
- **Solução:** Já parcialmente migrado para `CreateTransacaoUseCase`
- **Pendente:** Remover service após migração completa

##### **5. InstituicaoService**
- **Localização:** `application.service.InstituicaoService`
- **Problema:** CRUD simples sem valor agregado
- **Solução:** Use Cases específicos
- **Use Cases Necessários:**
  - `RegisterInstituicaoUseCase`
  - `ListInstituicoesUseCase`

#### **🟠 Factories no Domain (Violação)**

##### **1. TransacaoFactory**
- **Localização:** `domain.service.TransacaoFactory`
- **Problema:** Factory no domain com dependências de infrastructure
- **Solução:** Mover lógica para Use Case ou Domain Service puro

##### **2. AtivoFactory**
- **Localização:** `domain.service.AtivoFactory`
- **Problema:** Interface no domain, implementação com JPA
- **Solução:** Mover para application layer como Use Case

##### **3. RendaFactory**
- **Localização:** `domain.service.RendaFactory`
- **Problema:** Factory com dependências de repositories
- **Solução:** Refatorar para Domain Service puro

#### **🟡 Mappers Mal Posicionados**

##### **1. TipoMovimentacaoMapper**
- **Localização:** `domain.service.TipoMovimentacaoMapper`
- **Problema:** Mapper no domain
- **Solução:** Mover para infrastructure ou application

##### **2. OperacaoMapper**
- **Localização:** `infrastructure.mapper.OperacaoMapper`
- **Problema:** ✅ **Já está correto** (infrastructure)

#### **🔵 Batch Processing (Não-Hexagonal)**

##### **1. OperacaoItemProcessor**
- **Localização:** `application.batch.processor.OperacaoItemProcessor`
- **Problema:** Lógica de negócio no processor
- **Solução:** Usar Use Cases dentro do processor

##### **2. BatchConfig**
- **Localização:** `application.batch.config.BatchConfig`
- **Problema:** ✅ **Já corrigido** (usa Use Cases)

##### **3. CustomOperacaoItemReader**
- **Localização:** `application.batch.reader.CustomOperacaoItemReader`
- **Problema:** Acesso direto a repository
- **Solução:** Usar Use Case para leitura

#### **🟣 Exception Handling**

##### **1. GlobalExceptionHandler**
- **Localização:** `presentation.exception.GlobalExceptionHandler`
- **Problema:** ✅ **Já está correto** (presentation layer)

##### **2. Custom Exceptions**
- **Localização:** `domain.exception.*`
- **Problema:** ✅ **Já estão corretas** (domain layer)

#### **⚫ API Externa (Não-Resiliente)**

##### **1. ApiMarketPriceClient**
- **Localização:** `infrastructure.api.ApiMarketPriceClient`
- **Problema:** Dependência única do Yahoo Finance (instável)
- **Solução:** Interface com múltiplas implementações + Circuit Breaker

##### **2. MarketPrice Models**
- **Localização:** `infrastructure.api.model.*`
- **Problema:** ✅ **Já estão corretos** (infrastructure)

### **📊 Resumo de Normalização**

| Componente | Status Atual | Ação Necessária | Prioridade |
|------------|--------------|-----------------|------------|
| RendaVariavelService | ❌ Não-conforme | Migrar para Use Cases | Alta |
| AtivoFinanceiroService | ❌ Não-conforme | Migrar para Use Cases | Alta |
| PortfolioService | ❌ Não-conforme | Migrar para Use Cases | Alta |
| TransacaoFactory | ❌ Não-conforme | Refatorar para Use Case | Média |
| AtivoFactory | ❌ Não-conforme | Mover para Application | Média |
| RendaFactory | ❌ Não-conforme | Domain Service puro | Média |
| TipoMovimentacaoMapper | ❌ Não-conforme | Mover para Infrastructure | Baixa |
| OperacaoItemProcessor | ❌ Não-conforme | Usar Use Cases | Baixa |
| ApiMarketPriceClient | ❌ Não-resiliente | Interface + Circuit Breaker | Alta |
| TransacaoService | 🔄 Parcialmente migrado | Remover após migração | Baixa |

### **🎯 Plano de Normalização**

#### **Semana 11: Normalização Final**
1. **Dia 46:** Migrar Services restantes para Use Cases
2. **Dia 47:** Refatorar Factories para Domain Services puros
3. **Dia 48:** Normalizar Batch Processing
4. **Dia 49:** Limpar dependências circulares
5. **Dia 50:** Validação final da arquitetura hexagonal

---

## 🚀 Estratégia de Migração

### **🎯 Abordagem: Incremental Guiada por Views**

**Princípios:**
1. **Separação Clara:** Domain puro vs Infrastructure
2. **Migração Incremental:** Uma entidade por vez
3. **Compatibilidade:** Manter views funcionando durante migração
4. **Performance:** Resolver problema de % em tempo real
5. **Testes:** Cobertura completa em cada etapa

### **📋 Fases da Migração**

#### **🥇 FASE 1: Core de Gestão (2-3 semanas)**
**Entidades:** Portfolio + AtivoFinanceiro + RendaVariavel

**Por que começar aqui:**
- ✅ **2 Views existentes** (ações + FII)
- ✅ **Resolve performance** (% em tempo real → coluna no banco)
- ✅ **Base para tudo** (Portfolio é agregado raiz)
- ✅ **Alto valor** (core do sistema)

**Arquitetura Alvo:**
```
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                           │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   Portfolio     │    │  AtivoFinanceiro                │ │
│  │ (Aggregate Root)│    │  + PercentualCarteira (VO)     │ │
│  │ + TotalInvestido│    │  + RendimentoAcumulado (VO)    │ │
│  │ + Diversificacao│    │  + StatusInvestimento (VO)     │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                        │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │GetPortfolioUse  │    │  CalculatePortfolioUseCase      │ │
│  │Case             │    │  (% carteira → banco)           │ │
│  │ListAtivosUseCase│    │  UpdatePercentualUseCase        │ │
│  │AnalyzePortfolio │    │  (job assíncrono)               │ │
│  │UseCase          │    │                                 │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                       │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │PortfolioEntity  │    │  AtivoFinanceiroEntity          │ │
│  │RendaVariavelEnt │    │  + Repositories                 │ │
│  │ity              │    │  + Mappers                      │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

#### **🥈 FASE 2: Gestão de Impostos (1-2 semanas)**
**Entidades:** Transacao + CalculoImposto + Darf

**Funcionalidades:**
- ✅ **Cálculo automático de DARF** (15% sobre lucro > R$ 20k/mês)
- ✅ **Controle de prejuízos** (compensação)
- ✅ **Alertas de vencimento** (último dia útil do mês)

#### **🥉 FASE 3: Refatoração da API Externa (1 semana)**
**Objetivo:** Substituir Yahoo Finance por API confiável

**Componentes:**
- ✅ **Interface MarketDataProvider** (múltiplas fontes)
- ✅ **BRAPI Implementation** (API brasileira gratuita)
- ✅ **Scraping Fallback** (backup quando API falha)
- ✅ **Circuit Breaker** (Resilience4j)
- ✅ **Cache de preços** (Redis ou in-memory)

### **🏅 FASE 4: Entidades de Apoio (1-2 semanas)**
**Entidades:** Instituicao + Usuario + RendaFixa

#### **🏆 FASE 5: Análise Inteligente (2-3 semanas)**
**Funcionalidades:**
- ✅ **Análise de timing** (compra/venda)
- ✅ **Score de operações** (boas compras vs ruins)
- ✅ **Rebalanceamento** (sugestões)

---

## 📅 Cronograma Detalhado

### **🗓️ Semana 1-2: Preparação + Portfolio**

#### **Dia 1-2: Correções Críticas de Segurança**
- [ ] **SEC-002:** Corrigir Frame Options (SAMEORIGIN)
- [ ] **OBS-001:** Configurar Actuator (health, metrics, info)
- [ ] **DATA-001:** Implementar Flyway com baseline
- [ ] **🔄 Git:** Commit inicial das correções críticas

#### **Dia 3-5: Domain Models Puros**
- [ ] Criar `domain.model.Portfolio` (POJO puro)
- [ ] Criar `domain.model.AtivoFinanceiro` (POJO puro)
- [ ] Criar Value Objects:
  - [ ] `PercentualCarteira`
  - [ ] `ValorInvestido`
  - [ ] `RendimentoTotal`
  - [ ] `Ticker`

#### **Dia 6-7: Infrastructure Entities**
- [ ] Mover `domain.entity.*` → `infrastructure.persistence.entity.*`
- [ ] Criar `PortfolioEntity` (JPA)
- [ ] Criar `AtivoFinanceiroEntity` (JPA + coluna `percentual_carteira`)
- [ ] Adicionar migração Flyway para nova coluna

#### **Dia 8-10: Use Cases + Ports**
- [ ] Criar interfaces de ports:
  - [ ] `PortfolioRepository`
  - [ ] `AtivoFinanceiroRepository`
  - [ ] `MarketDataService`
- [ ] Implementar Use Cases:
  - [ ] `GetPortfolioUseCase`
  - [ ] `ListAtivosUseCase`
  - [ ] `CalculatePortfolioPercentagesUseCase`

### **🗓️ Semana 3: Adapters + Views**

#### **Dia 11-12: Repository Adapters**
- [ ] Implementar `PortfolioRepositoryAdapter`
- [ ] Implementar `AtivoFinanceiroRepositoryAdapter`
- [ ] Criar Mappers (Domain ↔ Entity)

#### **Dia 13-14: Job Assíncrono**
- [ ] Implementar `UpdatePortfolioPercentagesUseCase`
- [ ] Configurar `@Scheduled` (5 minutos)
- [ ] Testar performance de cálculo

#### **Dia 15: Migração das Views**
- [ ] Atualizar `GridwithFiltersAcoesView`
- [ ] Atualizar `GridwithFiltersFiiView`
- [ ] Remover cálculos em tempo real
- [ ] Usar percentuais pré-calculados

### **🗓️ Semana 4: RendaVariavel + Git**

#### **Dia 16-18: RendaVariavel**
- [ ] Criar `domain.model.RendaVariavel`
- [ ] Migrar `RendaVariavelEntity`
- [ ] Implementar Use Cases específicos
- [ ] Integrar com Portfolio
- [ ] **🔄 Git:** Commit da migração RendaVariavel

#### **Dia 19-21: Testes + Documentação**
- [ ] Testes unitários (Domain)
- [ ] Testes de integração (Use Cases)
- [ ] Testes de regressão (Views)
- [ ] Atualizar documentação
- [ ] **🔄 Git:** Commit dos testes

### **🗓️ Semana 5: Nova API de Preços**

#### **Dia 22-24: Refatoração da API**
- [ ] Interface `MarketDataProvider`
- [ ] Implementação BRAPI (https://brapi.dev/)
- [ ] Scraping fallback para B3
- [ ] Circuit Breaker com Resilience4j
- [ ] **🔄 Git:** Commit da nova API

#### **Dia 25-26: Cache e Performance**
- [ ] `UpdateMarketPricesUseCase`
- [ ] Cache de preços (TTL 5 minutos)
- [ ] Substituir Yahoo Finance completamente
- [ ] Testes de carga da API
- [ ] **🔄 Git:** Commit do sistema de cache

### **🗓️ Semana 6-7: Sistema de Impostos**

#### **Dia 26-28: Domain de Impostos**
- [ ] Criar `domain.model.CalculoImposto`
- [ ] Criar `domain.model.Darf`
- [ ] Value Objects para impostos

#### **Dia 29-32: Use Cases de Impostos**
- [ ] `CalculateDarfUseCase`
- [ ] `GetTaxObligationsUseCase`
- [ ] `CompensatePrejuizosUseCase`

#### **Dia 33-35: View de Impostos**
- [ ] Criar `ImpostosView`
- [ ] Dashboard de obrigações
- [ ] Alertas de vencimento

### **🗓️ Semana 8-10: Análise Inteligente + Normalização Final**

#### **Dia 36-40: Sistema de Análise**
- [ ] `AnalyzeOperationPerformanceUseCase`
- [ ] `GetBuySignalsUseCase`
- [ ] `GetSellSignalsUseCase`
- [ ] Algoritmos de análise técnica

#### **Dia 41-45: View de Análise**
- [ ] Criar `AnaliseView`
- [ ] Gráficos de performance
- [ ] Recomendações de compra/venda
- [ ] Score de operações
- [ ] **🔄 Git:** Commit do sistema de análise

### **🗓️ Semana 11: Normalização Hexagonal Final**

#### **Dia 46-50: Componentes Restantes**
- [ ] Migrar Services restantes para Use Cases
- [ ] Normalizar Exception Handling
- [ ] Refatorar Batch Processing
- [ ] Limpar dependências circulares
- [ ] **🔄 Git:** Commit final da normalização

---

## ⚙️ Configurações Necessárias

### **🔧 Flyway Setup**

#### **1. Dependências (pom.xml)**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

#### **2. Configuração (application.properties)**
```properties
# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
spring.flyway.locations=classpath:db/migration
spring.flyway.validate-on-migrate=true
spring.flyway.clean-disabled=true

# Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
```

#### **3. Estrutura de Diretórios**
```
src/main/resources/
└── db/
    └── migration/
        ├── V1__baseline.sql
        ├── V2__add_percentual_carteira_column.sql
        └── V3__create_impostos_tables.sql
```

### **🔒 Segurança**

#### **1. Externalizar Secrets**
```bash
# Variáveis de ambiente
export DB_PASSWORD=nB132MUlpZ4jxn7f
export API_ALPHA_KEY=BRRQ6MQO8CYYPM5M
```

```properties
# application.properties
spring.datasource.password=${DB_PASSWORD}
api.alpha.key=${API_ALPHA_KEY}
```

#### **2. Configurar Frame Options**
```java
// SecurityConfig.java
.headers(headers -> headers
    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
)
```

### **📊 Observabilidade**

#### **1. Actuator**
```properties
# Endpoints essenciais
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true
```

### **⚡ Performance**

#### **1. JPA Otimizado**
```properties
# Desabilitar Open-in-View
spring.jpa.open-in-view=false

# Otimizações de performance
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

#### **2. Lazy Loading**
```java
// Corrigir eager loading
@ElementCollection(fetch = FetchType.LAZY)
private Set<String> instituicoes;
```

---

## ✅ Checklist de Execução

### **🔴 Pré-Requisitos (Críticos)**
- [ ] **Backup completo** do banco de dados
- [ ] **Ambiente de teste** idêntico à produção
- [ ] **Variáveis de ambiente** configuradas
- [ ] **Flyway baseline** executado com sucesso
- [ ] **Testes de regressão** passando

### **📋 Fase 1: Portfolio + AtivoFinanceiro**

#### **Domain Layer**
- [ ] `domain.model.Portfolio` criado
- [ ] `domain.model.AtivoFinanceiro` criado
- [ ] Value Objects criados:
  - [ ] `PercentualCarteira`
  - [ ] `ValorInvestido`
  - [ ] `RendimentoTotal`
  - [ ] `Ticker`
- [ ] Invariantes implementados
- [ ] Testes unitários (100% cobertura)

#### **Application Layer**
- [ ] Ports definidos:
  - [ ] `PortfolioRepository`
  - [ ] `AtivoFinanceiroRepository`
  - [ ] `MarketDataService`
- [ ] Use Cases implementados:
  - [ ] `GetPortfolioUseCase`
  - [ ] `ListAtivosUseCase`
  - [ ] `CalculatePortfolioPercentagesUseCase`
- [ ] Commands e Results criados
- [ ] Testes de Use Cases (100% cobertura)

#### **Infrastructure Layer**
- [ ] Entities movidas para `infrastructure.persistence.entity`:
  - [ ] `PortfolioEntity`
  - [ ] `AtivoFinanceiroEntity`
- [ ] Coluna `percentual_carteira` adicionada
- [ ] Repository Adapters implementados
- [ ] Mappers criados (Domain ↔ Entity)
- [ ] Testes de integração

#### **Presentation Layer**
- [ ] Views atualizadas:
  - [ ] `GridwithFiltersAcoesView`
  - [ ] `GridwithFiltersFiiView`
- [ ] Cálculos em tempo real removidos
- [ ] Percentuais pré-calculados utilizados
- [ ] Testes de UI

#### **Jobs Assíncronos**
- [ ] `UpdatePortfolioPercentagesUseCase` implementado
- [ ] `@Scheduled` configurado (5 minutos)
- [ ] Logs de monitoramento
- [ ] Tratamento de erros robusto

### **📋 Fase 2: Sistema de Impostos**
- [ ] `domain.model.CalculoImposto` criado
- [ ] `domain.model.Darf` criado
- [ ] Use Cases de impostos implementados
- [ ] `ImpostosView` criada
- [ ] Alertas de vencimento funcionando

### **📋 Fase 3: Análise Inteligente**
- [ ] Algoritmos de análise implementados
- [ ] `AnaliseView` criada
- [ ] Score de operações funcionando
- [ ] Recomendações de compra/venda

### **🧪 Testes e Qualidade**
- [ ] **Cobertura de testes:** > 90%
- [ ] **Testes de performance:** < 100ms por consulta
- [ ] **Testes de carga:** 1000 req/min
- [ ] **Testes de segurança:** Sem vulnerabilidades
- [ ] **Testes de regressão:** Todas as funcionalidades

### **🚀 Deploy e Monitoramento**
- [ ] **Deploy em staging:** Sucesso
- [ ] **Testes de aceitação:** Aprovados
- [ ] **Monitoramento:** Métricas coletadas
- [ ] **Logs:** Estruturados e funcionais
- [ ] **Backup:** Estratégia de rollback testada

---

## ⚠️ Riscos e Mitigações

### **🔴 Riscos Críticos**

#### **1. Perda de Dados Durante Migração**
- **Probabilidade:** Baixa
- **Impacto:** Crítico
- **Mitigação:**
  - ✅ Backup completo antes de cada etapa
  - ✅ Testes em ambiente idêntico
  - ✅ Rollback automático em caso de falha
  - ✅ Validação de integridade pós-migração

#### **2. Performance Degradada**
- **Probabilidade:** Média
- **Impacto:** Alto
- **Mitigação:**
  - ✅ Testes de performance em cada etapa
  - ✅ Monitoramento em tempo real
  - ✅ Otimização de queries e índices
  - ✅ Cache estratégico

#### **3. Incompatibilidade de Views**
- **Probabilidade:** Média
- **Impacto:** Alto
- **Mitigação:**
  - ✅ Migração incremental
  - ✅ Testes de regressão automatizados
  - ✅ Manutenção de contratos de API
  - ✅ Feature flags para rollback

### **🟠 Riscos Moderados**

#### **4. Complexidade de Mapeamento**
- **Probabilidade:** Alta
- **Impacto:** Médio
- **Mitigação:**
  - ✅ Mappers bem testados
  - ✅ Validação de dados
  - ✅ Logs detalhados
  - ✅ Testes de conversão

#### **5. Prazo de Entrega**
- **Probabilidade:** Média
- **Impacto:** Médio
- **Mitigação:**
  - ✅ Cronograma realista
  - ✅ Entregas incrementais
  - ✅ Priorização por valor
  - ✅ Buffer de tempo

### **🟡 Riscos Baixos**

#### **6. Resistência à Mudança**
- **Probabilidade:** Baixa
- **Impacto:** Baixo
- **Mitigação:**
  - ✅ Documentação clara
  - ✅ Treinamento da equipe
  - ✅ Benefícios visíveis
  - ✅ Suporte contínuo

---

## 📊 Métricas de Sucesso

### **🎯 KPIs Técnicos**
- **Performance:** Tempo de carregamento < 100ms
- **Disponibilidade:** > 99.9%
- **Cobertura de Testes:** > 90%
- **Bugs em Produção:** < 1 por semana
- **Tempo de Build:** < 5 minutos

### **💰 KPIs de Negócio**
- **Cálculo de DARF:** Automático e preciso
- **Análise de Performance:** Insights acionáveis
- **Gestão de Carteira:** Rebalanceamento eficiente
- **Satisfação do Usuário:** > 4.5/5
- **Tempo de Processamento:** Import < 30 segundos

### **🔒 KPIs de Segurança**
- **Vulnerabilidades:** Zero críticas
- **Secrets Expostos:** Zero
- **Rate Limiting:** Funcionando
- **Monitoramento:** 100% cobertura
- **Backup:** Testado semanalmente

---

## 📚 Referências

### **Documentação Técnica**
- [Arquitetura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Spring Boot Best Practices](https://spring.io/guides)
- [Flyway Documentation](https://flywaydb.org/documentation/)

### **Documentação do Projeto**
- [Funcionalidades de Importação](funcionalidades/import.md)
- [Funcionalidades de Operação](funcionalidades/operacao.md)
- [Configuração do Flyway](flyway/README-flyway-setup.md)

### **Ferramentas e Tecnologias**
- **Spring Boot 3.5.5**
- **Java 21**
- **Vaadin 24.8+**
- **MySQL/MariaDB**
- **Flyway**
- **JUnit 5**
- **Mockito**

---

## 📝 Log de Alterações

| Data | Versão | Alteração | Autor |
|------|--------|-----------|-------|
| 01/09/2025 | 1.0 | Criação inicial do plano | Claude 4 Sonnet |
| | | Consolidação de achados de auditoria | |
| | | Definição de cronograma detalhado | |
| | | Estratégia de migração hexagonal | |

---

**🎯 Próximo Passo:** Executar correções críticas de segurança (Dia 1-2)

**📞 Contato:** Para dúvidas ou ajustes no plano, consulte a documentação ou abra uma issue.

---

*Este documento é um guia vivo e será atualizado conforme o progresso da migração.*