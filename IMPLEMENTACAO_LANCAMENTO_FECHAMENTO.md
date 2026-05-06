# 🛠️ Guia Prático de Implementação: Integração Lançamento → Fechamento

## Objetivo
Fazer com que **lançamentos (débitos e créditos) apareçam no fechamento** da conta do cliente.

---

## 1️⃣ Entender o Problema Atual

### Fluxo Quebrado
```
Cliente faz consumo  →  Lançamento aplicado  →  Fechamento gerado
       ✓                      ✓                        ❌
                                            (Lançamento é ignorado!)
```

### Exemplo Real
```
Cliente: João Silva

Consumo:
  - 10/05: Produto A (R$ 100)
  - 15/05: Produto B (R$ 50)
  Total de Consumos: R$ 150

Lançamento:
  - 12/05: Desconto Fidelidade (CREDITO, R$ 30)

Resultado ESPERADO: R$ 150 - R$ 30 = R$ 120
Resultado ATUAL:    R$ 150 (ignora o desconto) ❌
```

---

## 2️⃣ Verificar Estrutura Atual

### Tabelas Existentes

```sql
-- Tabela CONSUMO (OK)
SELECT * FROM consumo WHERE cliente_id = 1;
-- Colunas: id, cliente_id, produto_id, quantidade, valor_unitario, data_hora, pago, entrada_estoque_id

-- Tabela LANCAMENTO (INCOMPLETA)
SELECT * FROM lancamento WHERE cliente_id = 1;
-- Colunas: id, cliente_id, data_hora, natureza, categoria, valor, motivo, responsavel_username, lancamento_origem_id
-- ❌ FALTAM: fechamento_id, data_processamento
```

---

## 3️⃣ Criar Migration do Banco de Dados

Arquivo: `src/main/resources/db/migration/V11__adicionar_campos_lancamento_fechamento.sql`

```sql
-- Adicionar coluna de rastreamento de fechamento na tabela lancamento
ALTER TABLE lancamento 
ADD COLUMN fechamento_id BIGINT,
ADD COLUMN data_processamento TIMESTAMP;

-- Adicionar constraint (opcional, mas recomendado)
ALTER TABLE lancamento 
ADD CONSTRAINT fk_lancamento_fechamento 
    FOREIGN KEY (fechamento_id) 
    REFERENCES fechamento_conta(id) 
    ON DELETE SET NULL;

-- Criar índice para busca rápida
CREATE INDEX idx_lancamento_cliente_fechamento 
ON lancamento(cliente_id, fechamento_id);

-- Índice para buscar lançamentos não processados
CREATE INDEX idx_lancamento_nao_processado 
ON lancamento(cliente_id) 
WHERE fechamento_id IS NULL;
```

**Executar contra o banco:**
```bash
# Via Flyway (automático ao iniciar a aplicação)
# ou manualmente no PostgreSQL/H2
```

---

## 4️⃣ Atualizar Entity JPA

Arquivo: `src/main/java/com/megamanager/lancamento/adapter/persistence/LancamentoEntity.java`

```java
package com.megamanager.lancamento.adapter.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lancamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long clienteId;
    
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NaturezaLancamento natureza;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoriaLancamento categoria;
    
    @Column(nullable = false)
    private BigDecimal valor;
    
    private String motivo;
    
    @Column(nullable = false)
    private String responsavelUsername;
    
    private Long lancamentoOrigemId;
    
    // NOVOS CAMPOS ⬇️
    @Column(name = "fechamento_id")
    private Long fechamentoId;  // null enquanto não foi processado
    
    @Column(name = "data_processamento")
    private LocalDateTime dataProcessamento;  // Quando foi incluído no fechamento
}
```

---

## 5️⃣ Atualizar Domain Model

Arquivo: `src/main/java/com/megamanager/lancamento/domain/Lancamento.java`

```java
// ...existing imports...

@Getter
public class Lancamento {

    private Long id;
    private Long clienteId;
    private LocalDateTime dataHora;
    private NaturezaLancamento natureza;
    private CategoriaLancamento categoria;
    private BigDecimal valor;
    private String motivo;
    private String responsavelUsername;
    private Long lancamentoOrigemId;
    
    // NOVO ⬇️
    private Long fechamentoId;
    private LocalDateTime dataProcessamento;

    private Lancamento(Long id,
                       Long clienteId,
                       LocalDateTime dataHora,
                       NaturezaLancamento natureza,
                       CategoriaLancamento categoria,
                       BigDecimal valor,
                       String motivo,
                       String responsavelUsername,
                       Long lancamentoOrigemId,
                       Long fechamentoId,              // NOVO
                       LocalDateTime dataProcessamento) { // NOVO

        // ...validações existentes...
        
        this.id = id;
        this.clienteId = clienteId;
        this.dataHora = dataHora;
        this.natureza = natureza;
        this.categoria = categoria;
        this.valor = valor;
        this.motivo = motivo;
        this.responsavelUsername = responsavelUsername;
        this.lancamentoOrigemId = lancamentoOrigemId;
        this.fechamentoId = fechamentoId;              // NOVO
        this.dataProcessamento = dataProcessamento;    // NOVO
    }

    public static Lancamento criar(Long clienteId,
                                   LocalDateTime dataHora,
                                   NaturezaLancamento natureza,
                                   CategoriaLancamento categoria,
                                   BigDecimal valor,
                                   String motivo,
                                   String responsavelUsername) {
        return new Lancamento(
                null,
                clienteId,
                dataHora,
                natureza,
                categoria,
                valor,
                motivo,
                responsavelUsername,
                null,
                null,                    // NOVO: fechamentoId null
                null                     // NOVO: dataProcessamento null
        );
    }

    public static Lancamento reconstruir(Long id,
                                         Long clienteId,
                                         LocalDateTime dataHora,
                                         NaturezaLancamento natureza,
                                         CategoriaLancamento categoria,
                                         BigDecimal valor,
                                         String motivo,
                                         String responsavelUsername,
                                         Long lancamentoOrigemId,
                                         Long fechamentoId,              // NOVO
                                         LocalDateTime dataProcessamento) { // NOVO
        return new Lancamento(
                id,
                clienteId,
                dataHora,
                natureza,
                categoria,
                valor,
                motivo,
                responsavelUsername,
                lancamentoOrigemId,
                fechamentoId,
                dataProcessamento
        );
    }

    // ...existing methods...
}
```

---

## 6️⃣ Adicionar Métodos ao Repositório

Arquivo: `src/main/java/com/megamanager/lancamento/application/port/out/LancamentoRepository.java`

```java
package com.megamanager.lancamento.application.port.out;

import com.megamanager.lancamento.domain.Lancamento;
import java.util.List;
import java.util.Optional;

public interface LancamentoRepository {
    
    Lancamento salvar(Lancamento lancamento);
    
    Optional<Lancamento> buscarPorId(Long id);
    
    List<Lancamento> buscarPorCliente(Long clienteId);
    
    boolean existeEstornoParaOrigem(Long lancamentoOrigemId);
    
    // NOVOS MÉTODOS ⬇️
    
    /**
     * Busca lançamentos que ainda não foram incluídos em nenhum fechamento
     * (fechamento_id IS NULL)
     */
    List<Lancamento> buscarNaoProcessadosPorCliente(Long clienteId);
    
    /**
     * Marca um conjunto de lançamentos como processados em um fechamento
     */
    void marcarLancamentosComoProcessados(List<Long> lancamentoIds, Long fechamentoId);
}
```

---

## 7️⃣ Implementar Métodos no Adapter JPA

Arquivo: `src/main/java/com/megamanager/lancamento/adapter/persistence/LancamentoRepositoryAdapter.java`

```java
// ...existing code...

@Component
@RequiredArgsConstructor
public class LancamentoRepositoryAdapter implements LancamentoRepository {
    
    private final LancamentoJpaRepository jpa;
    private final LancamentoMapper mapper;
    
    // ...existing methods...
    
    // NOVOS MÉTODOS ⬇️
    
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
}
```

---

## 8️⃣ Atualizar JPA Repository Interface

Arquivo: `src/main/java/com/megamanager/lancamento/adapter/persistence/LancamentoJpaRepository.java`

```java
package com.megamanager.lancamento.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LancamentoJpaRepository extends JpaRepository<LancamentoEntity, Long> {
    
    List<LancamentoEntity> findByClienteId(Long clienteId);
    
    Optional<LancamentoEntity> findByLancamentoOrigemId(Long lancamentoOrigemId);
    
    // NOVOS ⬇️
    
    /**
     * Busca lançamentos não processados ordenados por data (mais antigos primeiro)
     */
    List<LancamentoEntity> findByClienteIdAndFechamentoIdIsNullOrderByDataHoraAsc(Long clienteId);
    
    /**
     * Atualiza múltiplos lançamentos em batch
     */
    @Modifying
    @Query("UPDATE LancamentoEntity l SET l.fechamentoId = :fechamentoId, l.dataProcessamento = :dataProcessamento " +
           "WHERE l.id IN :lancamentoIds")
    void updateFechamentoIdBatch(
        @Param("lancamentoIds") List<Long> lancamentoIds,
        @Param("fechamentoId") Long fechamentoId,
        @Param("dataProcessamento") LocalDateTime dataProcessamento
    );
}
```

---

## 9️⃣ Atualizar ItemFechamento

Arquivo: `src/main/java/com/megamanager/fechamento/domain/ItemFechamento.java`

```java
package com.megamanager.fechamento.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import com.megamanager.lancamento.domain.NaturezaLancamento;

@Getter
@Builder
@AllArgsConstructor
public class ItemFechamento {
    
    // Campos existentes
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    
    // NOVOS CAMPOS ⬇️
    private String tipoItem;              // "CONSUMO" ou "LANCAMENTO"
    private Long lancamentoId;            // null se for consumo
    private String descricao;             // Descrição do lançamento
    private NaturezaLancamento natureza;  // DEBITO ou CREDITO (null para consumos)
}
```

---

## 🔟 Refatorar FechamentoContaService

Arquivo: `src/main/java/com/megamanager/fechamento/application/usecase/FechamentoContaService.java`

```java
package com.megamanager.fechamento.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.fechamento.application.port.in.BuscarFechamentoUseCase;
import com.megamanager.fechamento.application.port.in.FecharContaClienteUseCase;
import com.megamanager.fechamento.application.port.in.ListarFechamentosUseCase;
import com.megamanager.fechamento.application.port.out.FechamentoContaRepository;
import com.megamanager.fechamento.domain.FechamentoConta;
import com.megamanager.fechamento.domain.ItemFechamento;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;  // NOVO
import com.megamanager.lancamento.domain.Lancamento;                           // NOVO
import com.megamanager.lancamento.domain.NaturezaLancamento;                   // NOVO
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class FechamentoContaService implements
        FecharContaClienteUseCase,
        ListarFechamentosUseCase,
        BuscarFechamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(FechamentoContaService.class);

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ConsumoRepository consumoRepository;
    private final FechamentoContaRepository fechamentoRepository;
    private final LancamentoRepository lancamentoRepository;  // NOVO

    @Override
    @Transactional
    public FechamentoConta fechar(Long clienteId) {
        log.info("Iniciando fechamento de conta para cliente: {}", clienteId);
        
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        // Buscar consumos abertos
        List<Consumo> consumosAbertos = consumoRepository.buscarNaoPagosPorCliente(clienteId);
        
        // NOVO: Buscar lançamentos não processados ⬇️
        List<Lancamento> lancamentosAbertos = lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId);

        if (consumosAbertos.isEmpty() && lancamentosAbertos.isEmpty()) {  // NOVO: verifica ambos
            throw new IllegalStateException("Cliente não possui consumos ou lançamentos em aberto");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "sistema";

        List<ItemFechamento> itens = new ArrayList<>();  // NOVO: usar ArrayList para adicionar itens

        // ========== ITENS DE CONSUMO (existente) ==========
        consumosAbertos.stream().map(consumo -> {
            Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            BigDecimal valorUnitario = calcularValorUnitario(cliente, consumo, produto);
            BigDecimal valorTotal = valorUnitario.multiply(
                    BigDecimal.valueOf(consumo.getDadosProduto().getQuantidade()));

            return ItemFechamento.builder()
                    .tipoItem("CONSUMO")              // NOVO
                    .produtoId(produto.getId())
                    .nomeProduto(produto.getNome())
                    .quantidade(consumo.getDadosProduto().getQuantidade())
                    .valorUnitario(valorUnitario)
                    .valorTotal(valorTotal)
                    .build();
        }).forEach(itens::add);

        // ========== ITENS DE LANÇAMENTO (NOVO) ⬇️ ==========
        lancamentosAbertos.stream().map(lancamento -> {
            BigDecimal valor = lancamento.getNatureza() == NaturezaLancamento.DEBITO
                    ? lancamento.getValor()                   // Positivo para débito
                    : lancamento.getValor().negate();         // Negativo para crédito

            String descricao = lancamento.getCategoria().name() + ": " + 
                    (lancamento.getMotivo() != null ? lancamento.getMotivo() : "");

            log.debug("Incluindo lançamento no fechamento: {} - {}", 
                    lancamento.getId(), descricao);

            return ItemFechamento.builder()
                    .tipoItem("LANCAMENTO")
                    .lancamentoId(lancamento.getId())
                    .descricao(descricao)
                    .valorTotal(valor)
                    .natureza(lancamento.getNatureza())
                    .build();
        }).forEach(itens::add);

        // ========== CALCULAR TOTAL ==========
        BigDecimal total = itens.stream()
                .map(ItemFechamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Fechamento calculado - Total de itens: {}, Valor total: {}", itens.size(), total);

        // ========== CRIAR FECHAMENTO ==========
        FechamentoConta fechamento = FechamentoConta.builder()
                .clienteId(cliente.getId())
                .clienteNome(cliente.getNome())
                .usuarioUsername(username)
                .dataHora(LocalDateTime.now())
                .totalPago(total)
                .itens(itens)
                .build();

        FechamentoConta salvo = fechamentoRepository.salvar(fechamento);

        // ========== MARCAR COMO PROCESSADOS ==========
        consumoRepository.marcarConsumosComoPagos(clienteId);
        
        // NOVO: Marcar lançamentos como processados ⬇️
        if (!lancamentosAbertos.isEmpty()) {
            lancamentoRepository.marcarLancamentosComoProcessados(
                lancamentosAbertos.stream()
                        .map(Lancamento::getId)
                        .collect(Collectors.toList()),
                salvo.getId()
            );
            log.info("Marcados {} lançamentos como processados", lancamentosAbertos.size());
        }

        log.info("Fechamento concluído com sucesso: id={}", salvo.getId());
        return salvo;
    }

    @Override
    public List<FechamentoConta> listar(Long clienteId, LocalDateTime de, LocalDateTime ate) {
        return fechamentoRepository.listar(clienteId, de, ate);
    }

    @Override
    public Optional<FechamentoConta> buscarPorId(Long fechamentoId) {
        return fechamentoRepository.buscarPorId(fechamentoId);
    }

    private BigDecimal calcularValorUnitario(Cliente cliente, Consumo consumo, Produto produto) {
        if (cliente.getPerfil() == PerfilCliente.SOCIO && consumo.getDadosProduto().getValorUnitario() != null) {
            return consumo.getDadosProduto().getValorUnitario();
        } else {
            return produto.getPrecoVenda();
        }
    }
}
```

---

## 1️⃣1️⃣ Testar a Implementação

### Teste Unitário

Arquivo: `src/test/java/com/megamanager/fechamento/application/usecase/FechamentoComLancamentoTest.java`

```java
package com.megamanager.fechamento.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.consumo.domain.DadosProduto;
import com.megamanager.fechamento.application.port.out.FechamentoContaRepository;
import com.megamanager.fechamento.domain.FechamentoConta;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

class FechamentoComLancamentoTest {

    private FechamentoContaService service;
    private ClienteRepository clienteRepository;
    private ConsumerRepository consumoRepository;
    private LancamentoRepository lancamentoRepository;
    private FechamentoContaRepository fechamentoRepository;
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setup() {
        clienteRepository = mock(ClienteRepository.class);
        consumoRepository = mock(ConsumoRepository.class);
        lancamentoRepository = mock(LancamentoRepository.class);
        fechamentoRepository = mock(FechamentoContaRepository.class);
        produtoRepository = mock(ProdutoRepository.class);

        service = new FechamentoContaService(
                clienteRepository,
                produtoRepository,
                consumoRepository,
                fechamentoRepository,
                lancamentoRepository
        );
    }

    @Test
    @DisplayName("Deve incluir lançamento de desconto no fechamento")
    void testarFechamentoComDesconto() {
        // Arrange
        Long clienteId = 1L;
        Cliente cliente = Cliente.reconstruir(clienteId, "João", "joao@email.com", "11987654321", PerfilCliente.COMUM);
        
        Consumo consumo = Consumo.criar(clienteId, 
                new DadosProduto(10L, 2, BigDecimal.TEN, BigDecimal.valueOf(20)),
                LocalDateTime.now(), null);
        
        Lancamento desconto = Lancamento.criar(clienteId,
                LocalDateTime.now(),
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.DESCONTO,
                new BigDecimal("5.00"),
                "Desconto fidelidade",
                "admin");

        Produto produto = Produto.reconstruir(10L, "Produto A", BigDecimal.TEN, true);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(consumoRepository.buscarNaoPagosPorCliente(clienteId)).thenReturn(List.of(consumo));
        when(lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId)).thenReturn(List.of(desconto));
        when(produtoRepository.buscarPorId(10L)).thenReturn(Optional.of(produto));
        when(fechamentoRepository.salvar(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        FechamentoConta resultado = service.fechar(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("15.00"), resultado.getTotalPago());  // 20 - 5
        assertEquals(2, resultado.getItens().size());  // 1 consumo + 1 lançamento
        
        assertTrue(resultado.getItens().stream()
                .anyMatch(i -> "CONSUMO".equals(i.getTipoItem())));
        assertTrue(resultado.getItens().stream()
                .anyMatch(i -> "LANCAMENTO".equals(i.getTipoItem())));

        verify(lancamentoRepository).marcarLancamentosComoProcessados(any(), eq(resultado.getId()));
    }

    @Test
    @DisplayName("Deve incluir múltiplos lançamentos (débito e crédito)")
    void testarMultiplosLancamentos() {
        // Arrange
        Long clienteId = 2L;
        Cliente cliente = Cliente.reconstruir(clienteId, "Maria", "maria@email.com", "11987654321", PerfilCliente.COMUM);
        
        Consumo consumo = Consumo.criar(clienteId, 
                new DadosProduto(20L, 1, BigDecimal.valueOf(100), BigDecimal.valueOf(100)),
                LocalDateTime.now(), null);
        
        Lancamento desconto = Lancamento.criar(clienteId,
                LocalDateTime.now(),
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.DESCONTO,
                new BigDecimal("10.00"),
                "Desconto",
                "admin");

        Lancamento cobranca = Lancamento.criar(clienteId,
                LocalDateTime.now(),
                NaturezaLancamento.DEBITO,
                CategoriaLancamento.COBRANCA_ADICIONAL,
                new BigDecimal("5.00"),
                "Taxa",
                "admin");

        Produto produto = Produto.reconstruir(20L, "Produto B", BigDecimal.valueOf(100), true);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(consumoRepository.buscarNaoPagosPorCliente(clienteId)).thenReturn(List.of(consumo));
        when(lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId)).thenReturn(List.of(desconto, cobranca));
        when(produtoRepository.buscarPorId(20L)).thenReturn(Optional.of(produto));
        when(fechamentoRepository.salvar(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        FechamentoConta resultado = service.fechar(clienteId);

        // Assert
        assertNotNull(resultado);
        // 100 (consumo) - 10 (desconto) + 5 (cobrança) = 95
        assertEquals(new BigDecimal("95.00"), resultado.getTotalPago());
        assertEquals(3, resultado.getItens().size());  // 1 consumo + 2 lançamentos
    }
}
```

---

## 1️⃣2️⃣ Checklist de Implementação

- [ ] **Banco de Dados**: Migration criada e executada
- [ ] **Entity JPA**: `LancamentoEntity` atualizada com `fechamento_id` e `data_processamento`
- [ ] **Domain Model**: `Lancamento` atualizado com novos campos
- [ ] **Repositório Interface**: Métodos `buscarNaoProcessadosPorCliente()` e `marcarLancamentosComoProcessados()` adicionados
- [ ] **JPA Repository**: Query methods implementados
- [ ] **Adapter**: Métodos de repositório implementados
- [ ] **Domain**: `ItemFechamento` atualizado com `tipoItem`, `lancamentoId`, `descricao`, `natureza`
- [ ] **Service**: `FechamentoContaService` refatorado para incluir lançamentos
- [ ] **Testes**: Testes unitários cobrindo os novos cenários
- [ ] **Validação**: Testes de integração passando

---

## 1️⃣3️⃣ Verificação Final

Após implementar, execute:

```bash
# Compilar
mvn clean compile

# Rodar testes
mvn test

# Executar a aplicação
mvn spring-boot:run

# Testar via API (exemplo)
curl -X POST http://localhost:8080/mega-manager-backend/fechamentos \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 1}'
```

---

## 🎉 Resultado Esperado

```json
{
  "id": 123,
  "clienteId": 1,
  "clienteNome": "João Silva",
  "usuarioUsername": "admin",
  "dataHora": "2026-05-04T15:30:00",
  "totalPago": 65.00,
  "itens": [
    {
      "tipoItem": "CONSUMO",
      "produtoId": 10,
      "nomeProduto": "Produto A",
      "quantidade": 2,
      "valorUnitario": 10.00,
      "valorTotal": 20.00
    },
    {
      "tipoItem": "CONSUMO",
      "produtoId": 11,
      "nomeProduto": "Produto B",
      "quantidade": 1,
      "valorUnitario": 30.00,
      "valorTotal": 30.00
    },
    {
      "tipoItem": "CONSUMO",
      "produtoId": 12,
      "nomeProduto": "Produto C",
      "quantidade": 3,
      "valorUnitario": 15.00,
      "valorTotal": 45.00
    },
    {
      "tipoItem": "LANCAMENTO",
      "lancamentoId": 50,
      "descricao": "DESCONTO: Fidelidade",
      "valorTotal": -30.00,
      "natureza": "CREDITO"
    }
  ]
}
```

✅ **Agora o desconto de R$ 30 é considerado!**


