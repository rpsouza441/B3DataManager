# Configuração do Flyway para B3DataManager

## Objetivo
Implementar controle de versão de schema de banco de dados usando Flyway para garantir consistência entre ambientes.

## Passos de Implementação

### 1. Adicionar Dependência no pom.xml

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

### 2. Configurar application.properties

```properties
# Configuração do Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
spring.flyway.locations=classpath:db/migration
spring.flyway.sql-migration-prefix=V
spring.flyway.sql-migration-separator=__
spring.flyway.sql-migration-suffixes=.sql
spring.flyway.validate-on-migrate=true
spring.flyway.clean-disabled=true

# Desabilitar DDL automático do Hibernate
spring.jpa.hibernate.ddl-auto=validate
```

### 3. Estrutura de Diretórios

```
src/main/resources/
└── db/
    └── migration/
        ├── V1__baseline.sql
        ├── V2__add_indexes.sql
        └── V3__future_changes.sql
```

### 4. Migração do Estado Atual

1. **Backup do banco atual:**
   ```bash
   mysqldump -u user -p financeiro > backup_pre_flyway.sql
   ```

2. **Copiar V1__baseline.sql para src/main/resources/db/migration/**

3. **Executar baseline:**
   ```bash
   ./mvnw flyway:baseline
   ```

4. **Alterar hibernate.ddl-auto para validate:**
   ```properties
   spring.jpa.hibernate.ddl-auto=validate
   ```

### 5. Comandos Úteis do Flyway

```bash
# Verificar status das migrações
./mvnw flyway:info

# Executar migrações pendentes
./mvnw flyway:migrate

# Validar migrações
./mvnw flyway:validate

# Reparar metadata (use com cuidado)
./mvnw flyway:repair
```

### 6. Convenções de Nomenclatura

- **V{versão}__{descrição}.sql** - Migrações versionadas
- **U{versão}__{descrição}.sql** - Rollbacks (não recomendado)
- **R__{descrição}.sql** - Migrações repetíveis

Exemplos:
- `V1__baseline.sql`
- `V2__add_user_audit_fields.sql`
- `V3__create_portfolio_indexes.sql`
- `R__update_user_view.sql`

### 7. Boas Práticas

1. **Nunca alterar migrações já aplicadas**
2. **Sempre testar em ambiente de desenvolvimento primeiro**
3. **Usar transações quando possível**
4. **Incluir rollback manual quando necessário**
5. **Documentar mudanças complexas**

### 8. Exemplo de Nova Migração

```sql
-- V2__add_audit_fields.sql
-- Adiciona campos de auditoria nas tabelas principais

ALTER TABLE usuario 
ADD COLUMN created_by VARCHAR(50),
ADD COLUMN updated_by VARCHAR(50);

ALTER TABLE ativo_financeiro 
ADD COLUMN created_by VARCHAR(50),
ADD COLUMN updated_by VARCHAR(50);

-- Criar índices para campos de auditoria
CREATE INDEX idx_usuario_created_by ON usuario(created_by);
CREATE INDEX idx_ativo_created_by ON ativo_financeiro(created_by);
```

### 9. Configuração por Ambiente

**application-dev.properties:**
```properties
spring.flyway.clean-disabled=false
spring.jpa.hibernate.ddl-auto=validate
```

**application-prod.properties:**
```properties
spring.flyway.clean-disabled=true
spring.flyway.validate-on-migrate=true
spring.jpa.hibernate.ddl-auto=none
```

### 10. Troubleshooting

**Erro de checksum:**
```bash
./mvnw flyway:repair
```

**Migração falhou:**
1. Verificar logs detalhados
2. Corrigir script SQL
3. Executar repair se necessário
4. Re-executar migrate

**Schema fora de sincronia:**
1. Comparar schema atual com migrações
2. Criar migração de correção
3. Aplicar em ambiente de teste primeiro

## Cronograma de Implementação

- **Dia 1:** Configurar Flyway e executar baseline
- **Dia 2:** Testar em ambiente de desenvolvimento
- **Dia 3:** Aplicar em staging
- **Dia 4:** Deploy em produção (janela de manutenção)

## Riscos e Mitigações

- **Risco:** Falha na migração em produção
- **Mitigação:** Backup completo + teste em staging idêntico

- **Risco:** Inconsistência entre ambientes
- **Mitigação:** Validação automática + CI/CD pipeline

- **Risco:** Performance durante migração
- **Mitigação:** Executar em janela de manutenção + monitoramento