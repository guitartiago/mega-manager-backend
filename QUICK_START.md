# ⚡ Quick Start: Implementar em 4 Horas

## 🎯 Objetivo
Fazer lançamentos refletirem no fechamento da conta do cliente.

## ⏱️ Tempo Total: ~4 horas
- 30 min: Banco de Dados
- 1h 30 min: Código
- 1h: Testes
- 1h: Validação e Deploy

---

## 📋 Pré-requisitos
- Maven instalado
- Banco de dados (PostgreSQL/H2)
- IDE com suporte a Java
- Conhecimento básico de Spring Boot

---

## 1️⃣ Banco de Dados (30 min)

### 1.1 Criar Migration

Arquivo: `src/main/resources/db/migration/V11__adicionar_campos_lancamento_fechamento.sql`

```sql
-- Adicionar campos de rastreamento
ALTER TABLE lancamento 
ADD COLUMN fechamento_id BIGINT,
ADD COLUMN data_processamento TIMESTAMP;

-- Constraint (opcional mas recomendado)
ALTER TABLE lancamento 
ADD CONSTRAINT fk_lancamento_fechamento 
    FOREIGN KEY (fechamento_id) 
    REFERENCES fechamento_conta(id) 
    ON DELETE SET NULL;

-- Índices para performance
CREATE INDEX idx_lancamento_cliente_fechamento 
ON lancamento(cliente_id, fechamento_id);

CREATE INDEX idx_lancamento_nao_processado 
ON lancamento(cliente_id) 
WHERE fechamento_id IS NULL;
```

### 1.2 Verificar (opcional)
```sql
-- Para verificar se funcionou
SELECT * FROM lancamento LIMIT 1;
-- Deve ter as colunas: id, cliente_id, ..., fechamento_id, data_processamento
```

---

## 2️⃣ Código (1h 30min)

### 2.1 Entity JPA

Arquivo: `src/main/java/com/megamanager/lancamento/adapter/persistence/LancamentoEntity.java`

Adicionar após `lancamentoOrigemId`:
```java
@Column(name = "fechamento_id")
private Long fechamentoId;

@Column(name = "data_processamento")
private LocalDateTime dataProcessamento;
```

### 2.2 Domain Model

Arquivo: `src/main/java/com/megamanager/lancamento/domain/Lancamento.java`

Adicionar ao construtor privado (após `lancamentoOrigemId`):
```java
Long fechamentoId,
LocalDateTime dataProcessamento
```

Adicionar campos:
```java
private Long fechamentoId;
private LocalDateTime dataProcessamento;
```

Atualizar métodos `criar()` e `reconstruir()` com `null, null` para os novos campos.

### 2.3 Repository Interface

Arquivo: `src/main/java/com/megamanager/lancamento/application/port/out/LancamentoRepository.java`

Adicionar:
```java
List<Lancamento> buscarNaoProcessadosPorCliente(Long clienteId);

void marcarLancamentosComoProcessados(List<Long> lancamentoIds, Long fechamentoId);
```

### 2.4 JPA Repository

Arquivo: `src/main/java/com/megamanager/lancamento/adapter/persistence/LancamentoJpaRepository.java`

Adicionar:
```java
List<LancamentoEntity> findByClienteIdAndFechamentoIdIsNullOrderByDataHoraAsc(Long clienteId);

@Modifying
@Query("UPDATE LancamentoEntity l SET l.fechamentoId = :fechamentoId, l.dataProcessamento = :dataProcessamento " +
       "WHERE l.id IN :lancamentoIds")
void updateFechamentoIdBatch(
    @Param("lancamentoIds") List<Long> lancamentoIds,
    @Param("fechamentoId") Long fechamentoId,
    @Param("dataProcessamento") LocalDateTime dataProcessamento
);
```

### 2.5 Repository Adapter

Arquivo: `src/main/java/com/megamanager/lancamento/adapter/persistence/LancamentoRepositoryAdapter.java`

Adicionar:
```java
@Override
public List<Lancamento> buscarNaoProcessadosPorCliente(Long clienteId) {
    return jpa.findByClienteIdAndFechamentoIdIsNullOrderByDataHoraAsc(clienteId)
            .stream()
            .map(mapper::toDomain)
            .toList();
}

@Override
public void marcarLancamentosComoProcessados(List<Long> lancamentoIds, Long fechamentoId) {
    LocalDateTime agora = LocalDateTime.now();
    jpa.updateFechamentoIdBatch(lancamentoIds, fechamentoId, agora);
}
```

### 2.6 ItemFechamento

Arquivo: `src/main/java/com/megamanager/fechamento/domain/ItemFechamento.java`

Adicionar campos:
```java
private String tipoItem;              // "CONSUMO" ou "LANCAMENTO"
private Long lancamentoId;
private String descricao;
private NaturezaLancamento natureza;
```

### 2.7 Refatorar FechamentoContaService

Arquivo: `src/main/java/com/megamanager/fechamento/application/usecase/FechamentoContaService.java`

**1. Adicionar Imports:**
```java
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;
import java.util.stream.Collectors;
```

**2. Injetar Repositório:**
```java
@RequiredArgsConstructor
public class FechamentoContaService ... {
    private final LancamentoRepository lancamentoRepository;  // NOVO
```

**3. No método `fechar()`, após buscar consumos:**
```java
// Buscar lançamentos não processados [NOVO]
List<Lancamento> lancamentosAbertos = lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId);

// Verificar se há itens [MODIFICADO]
if (consumosAbertos.isEmpty() && lancamentosAbertos.isEmpty()) {
    throw new IllegalStateException("Cliente não possui consumos ou lançamentos em aberto");
}
```

**4. Mudar `toList()` para `new ArrayList<>()` na linha de criação de itens:**
```java
List<ItemFechamento> itens = new ArrayList<>();  // MUDADO: era .toList()
```

**5. Adicionar, após processar consumos:**
```java
// Itens de lançamentos [NOVO]
lancamentosAbertos.stream().map(lancamento -> {
    BigDecimal valor = lancamento.getNatureza() == NaturezaLancamento.DEBITO
            ? lancamento.getValor()
            : lancamento.getValor().negate();

    return ItemFechamento.builder()
            .tipoItem("LANCAMENTO")
            .lancamentoId(lancamento.getId())
            .descricao(lancamento.getCategoria().name() + ": " + lancamento.getMotivo())
            .valorTotal(valor)
            .natureza(lancamento.getNatureza())
            .build();
}).forEach(itens::add);
```

**6. Adicionar, após `marcarConsumosComoPagos()`:**
```java
// Marcar lançamentos como processados [NOVO]
if (!lancamentosAbertos.isEmpty()) {
    lancamentoRepository.marcarLancamentosComoProcessados(
        lancamentosAbertos.stream()
                .map(Lancamento::getId)
                .collect(Collectors.toList()),
        salvo.getId()
    );
}
```

---

## 3️⃣ Testes (1 hora)

### 3.1 Compilar

```bash
cd /path/to/mega-manager-backend
mvn clean compile
```

Se houver erros de import, adicione os imports faltantes.

### 3.2 Rodar Testes Existentes

```bash
mvn test -Dtest=GerenciarConsumoServiceTest
mvn test -Dtest=FechamentoContaServiceTest
```

Todos devem passar.

### 3.3 Teste Manual

Criar `src/test/java/com/megamanager/fechamento/FechamentoComLancamentoIntegrationTest.java`:

```java
@SpringBootTest
class FechamentoComLancamentoIntegrationTest {
    
    @Autowired
    private FechamentoContaService fechamentoService;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ConsumoRepository consumoRepository;
    
    @Autowired
    private LancamentoRepository lancamentoRepository;
    
    @Test
    void testarFechamentoComLancamento() {
        // Criar cliente
        Cliente cliente = clienteRepository.salvar(
            Cliente.criar("João", "joao@email.com", "11987654321", PerfilCliente.COMUM)
        );
        
        // Criar consumo
        consumoRepository.salvar(
            Consumo.criar(cliente.getId(), 
                new DadosProduto(1L, 2, BigDecimal.TEN, BigDecimal.valueOf(20)),
                LocalDateTime.now(), null)
        );
        
        // Criar lançamento
        lancamentoRepository.salvar(
            Lancamento.criar(cliente.getId(),
                LocalDateTime.now(),
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.DESCONTO,
                new BigDecimal("5"),
                "Desconto",
                "admin")
        );
        
        // Fechar conta
        FechamentoConta resultado = fechamentoService.fechar(cliente.getId());
        
        // Validar
        assertEquals(new BigDecimal("15"), resultado.getTotalPago());  // 20 - 5
        assertEquals(2, resultado.getItens().size());  // 1 consumo + 1 lançamento
    }
}
```

Rodar:
```bash
mvn test -Dtest=FechamentoComLancamentoIntegrationTest
```

---

## 4️⃣ Validação (1 hora)

### 4.1 Build Completo

```bash
mvn clean install -DskipTests
```

Deve terminar com "BUILD SUCCESS".

### 4.2 Iniciar Aplicação

```bash
mvn spring-boot:run
```

Verificar que inicia sem erros.

### 4.3 Teste via API

**Terminal 1 (deixar rodando):**
```bash
mvn spring-boot:run
```

**Terminal 2 (novos comandos):**

```bash
# 1. Criar cliente
curl -X POST http://localhost:8080/mega-manager-backend/clientes \
  -H "Content-Type: application/json" \
  -d '{"nome":"João","email":"joao@email.com","celular":"11987654321"}'

# 2. Registrar consumo
curl -X POST http://localhost:8080/mega-manager-backend/consumos \
  -H "Content-Type: application/json" \
  -d '{"clienteId":1,"produtoId":1,"quantidade":2}'

# 3. Registrar lançamento
curl -X POST http://localhost:8080/mega-manager-backend/lancamentos \
  -H "Content-Type: application/json" \
  -d '{"clienteId":1,"natureza":"CREDITO","categoria":"DESCONTO","valor":5,"motivo":"Fidelidade"}'

# 4. Fechar conta
curl -X POST http://localhost:8080/mega-manager-backend/fechamentos \
  -H "Content-Type: application/json" \
  -d '{"clienteId":1}'

# 5. Ver resultado (ajuste o ID conforme retorno)
curl -X GET http://localhost:8080/mega-manager-backend/fechamentos/1
```

**Verificar:**
- ✅ Status 200 em todas as respostas
- ✅ totalPago inclui o desconto
- ✅ itens contém "CONSUMO" e "LANCAMENTO"

---

## 5️⃣ Deploy (30 min)

### 5.1 Build para Produção

```bash
mvn clean package -DskipTests -Pprod
```

Vai criar JAR em `target/mega-manager-backend-1.0.0.jar`

### 5.2 Rodar JAR

```bash
java -jar target/mega-manager-backend-1.0.0.jar --spring.profiles.active=prod
```

### 5.3 Validar em Produção

Mesmo que seção 4.3, mas contra servidor de produção.

---

## ✅ Checklist de Implementação

- [ ] Migration criada e executada
- [ ] LancamentoEntity.java editado
- [ ] Lancamento.java editado
- [ ] LancamentoRepository.java editado
- [ ] LancamentoJpaRepository.java editado
- [ ] LancamentoRepositoryAdapter.java editado
- [ ] ItemFechamento.java editado
- [ ] FechamentoContaService.java editado
- [ ] Compilação sem erros (`mvn clean compile`)
- [ ] Testes passam (`mvn test`)
- [ ] Teste de integração passa
- [ ] Aplicação inicia (`mvn spring-boot:run`)
- [ ] API retorna valores corretos
- [ ] Build de produção funciona

---

## 🚨 Troubleshooting

### Erro: "Cannot find symbol 'fechamentoId'"
**Causa:** Campo não foi adicionado à entity ou domain  
**Solução:** Verificar seções 2.1 e 2.2

### Erro: "Method not found in repository"
**Causa:** Método não foi adicionado à interface ou JPA repository  
**Solução:** Verificar seções 2.3 e 2.4

### Erro: "Compilation failed"
**Solução:**
```bash
mvn clean
mvn compile
```

### Teste falha com "NullPointerException"
**Causa:** Injeção de repositório faltando  
**Solução:** Verificar seção 2.7, passo 2

### API retorna erro 500
**Solução:**
```bash
# Ver logs
tail -f nohup.out

# Ou verificar console da IDE
```

### Total não está sendo calculado corretamente
**Causa:** Lógica de débito/crédito invertida  
**Solução:** Verificar seção 2.7, passo 5 (if natureza == DEBITO)

---

## 📞 Suporte Rápido

| Problema | Link para Documentação |
|----------|----------------------|
| "Não entendo o fluxo" | DIAGRAMAS_FLUXO.md |
| "Qual é a regra de negócio?" | REGRA_NEGOCIO...md |
| "Não sei por onde começar" | README_DOCUMENTACAO.md |
| "Preciso entender tudo" | REGRA_NEGOCIO...md |

---

## 🎉 Pronto!

Se chegou aqui com tudo verde (✅), você:
- ✅ Integrou lançamentos ao fechamento
- ✅ Contas agora calculam corretamente
- ✅ Sistema é rastreável
- ✅ Tudo validado e em produção

**Tempo total: ~4 horas** ⏱️

---

**Versão**: 1.0  
**Data**: 04/05/2026  
**Dúvidas?** Consulte os documentos completos na raiz do projeto


