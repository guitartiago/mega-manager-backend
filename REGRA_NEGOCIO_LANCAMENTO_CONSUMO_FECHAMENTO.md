# 📊 Regra de Negócio: Lançamento, Consumo e Fechamento

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Módulo de Lançamento](#módulo-de-lançamento)
3. [Módulo de Consumo](#módulo-de-consumo)
4. [Módulo de Fechamento](#módulo-de-fechamento)
5. [Fluxo Integrado](#fluxo-integrado)
6. [Problemas Identificados](#problemas-identificados)
7. [Solução Proposta](#solução-proposta)

---

## 🎯 Visão Geral

O sistema MegaManager controla as contas dos clientes através de três módulos principais:

1. **Lançamento**: Registro de débitos e créditos na conta do cliente
2. **Consumo**: Registro de produtos/serviços consumidos pelo cliente
3. **Fechamento**: Geração da conta final com o valor total a pagar

### Estado Atual ❌
- **Lançamentos** e **Consumos** são tratados **INDEPENDENTEMENTE**
- **Fechamento** considera APENAS consumos não pagos
- **Lançamentos são ignorados** no cálculo final da conta

### Problema Principal
Um cliente que tem:
- 5 consumos de R$ 10 cada = R$ 50
- 1 desconto (lançamento CREDITO) de R$ 20

**Resultado esperado:** Conta de R$ 30
**Resultado atual:** Conta de R$ 50 (lançamento ignorado! ❌)

---

## 📝 Módulo de Lançamento

### Estrutura de Dados

```java
class Lancamento {
    Long id;                          // ID único
    Long clienteId;                   // Cliente afetado
    LocalDateTime dataHora;           // Quando foi criado
    NaturezaLancamento natureza;      // DEBITO ou CREDITO
    CategoriaLancamento categoria;    // Tipo do lançamento
    BigDecimal valor;                 // Valor (sempre positivo)
    String motivo;                    // Razão do lançamento
    String responsavelUsername;       // Quem criou
    Long lancamentoOrigemId;          // Para ESTORNO: aponta para a origem
}
```

### Natureza do Lançamento

```java
enum NaturezaLancamento {
    DEBITO,   // Aumenta o valor a pagar (positivo)
    CREDITO   // Diminui o valor a pagar (negativo)
}
```

### Categoria do Lançamento

```java
enum CategoriaLancamento {
    PAGAMENTO,              // Cliente pagou (CREDITO)
    SERVICO,                // Serviço prestado (DEBITO)
    DESCONTO,               // Desconto concedido (CREDITO) - requer ADMIN
    CORRECAO,               // Correção de erro (DEBITO/CREDITO) - requer ADMIN
    COBRANCA_ADICIONAL,     // Multa/Taxa adicional (DEBITO)
    ESTORNO                 // Reversão de lançamento (DEBITO ou CREDITO invertido)
}
```

### Exemplos de Lançamentos

| Categoria | Natureza | Valor | Motivo | Efeito |
|-----------|----------|-------|--------|--------|
| PAGAMENTO | CREDITO | 100 | Pagamento recebido | -R$ 100 |
| DESCONTO | CREDITO | 20 | Fidelidade | -R$ 20 |
| CORRECAO | DEBITO | 5 | Erro em medição | +R$ 5 |
| COBRANCA_ADICIONAL | DEBITO | 10 | Multa por atraso | +R$ 10 |
| SERVICO | DEBITO | 50 | Manutenção | +R$ 50 |

---

## 🛒 Módulo de Consumo

### Estrutura de Dados

```java
class Consumo {
    Long id;                          // ID único
    Long clienteId;                   // Cliente que consumiu
    DadosProduto dadosProduto;        // Produto e quantidade
    LocalDateTime dataHora;           // Quando foi consumido
    boolean pago;                     // Se já foi incluído em fechamento
    Long entradaEstoqueId;            // Rastreamento de estoque (FIFO)
}

class DadosProduto {
    Long produtoId;                   // Qual produto
    Integer quantidade;               // Quanto
    BigDecimal valorUnitario;         // Preço cobrado (especial para sócios)
    BigDecimal valorTotal;            // Calculado automaticamente
}
```

### Regra de Preço no Consumo

```java
// No momento do consumo
if (cliente.perfil == SOCIO) {
    // Sócio sempre paga o preço de custo
    dadosProduto.valorUnitario = valorEspecial;  // Definido no ato
} else {
    // Comum paga preço de venda
    dadosProduto.valorUnitario = produto.precoVenda;
}
```

### Status do Consumo

- **pago = false**: Consumo em aberto, será incluído no próximo fechamento
- **pago = true**: Já foi incluído em um fechamento

---

## 💰 Módulo de Fechamento

### Estrutura de Dados

```java
class FechamentoConta {
    Long id;                          // ID do fechamento
    Long clienteId;                   // Cliente
    String clienteNome;               // Nome (denormalizado)
    String usuarioUsername;           // Quem fechou
    LocalDateTime dataHora;           // Data do fechamento
    BigDecimal totalPago;             // Total da conta
    List<ItemFechamento> itens;       // Detalhes dos consumos
}

class ItemFechamento {
    Long produtoId;
    String nomeProduto;
    Integer quantidade;
    BigDecimal valorUnitario;         // Preço cobrado
    BigDecimal valorTotal;            // quantidade × valorUnitario
}
```

### Fluxo Atual de Fechamento ❌

```
1. Buscar TODOS os consumos não pagos (pago=false)
   SELECT * FROM consumo WHERE clienteId=X AND pago=false

2. Para cada consumo:
   - Calcular valor unitário (se sócio: usa valor especial; se não: preço de venda)
   - Calcular valor total (quantidade × valor unitário)
   - Criar ItemFechamento

3. Somar todos os valores totais
   totalPago = SUM(itemFechamento.valorTotal)

4. Salvar FechamentoConta
5. Marcar todos os consumos como pago (pago=true)
6. ❌ LANÇAMENTOS SÃO IGNORADOS!
```

---

## 🔄 Fluxo Integrado (Atual - COM PROBLEMAS)

```
┌─────────────────────────────────────────────────────────┐
│ Timeline do Cliente                                     │
├─────────────────────────────────────────────────────────┤
│ 01/05: Consumo de R$ 100 (pago=false)                   │
│ 02/05: Consumo de R$ 50 (pago=false)                    │
│ 03/05: Lançamento DESCONTO -R$ 30 (CREDITO)             │
│ 04/05: Consumo de R$ 20 (pago=false)                    │
│ 05/05: Fechamento da Conta                              │
└─────────────────────────────────────────────────────────┘

Cálculo ATUAL (ERRADO):
├─ Consumo 01/05: R$ 100 ✓
├─ Consumo 02/05: R$ 50 ✓
├─ LANÇAMENTO 03/05: -R$ 30 ✗ IGNORADO!
├─ Consumo 04/05: R$ 20 ✓
├─ TOTAL = R$ 170 ❌ (DEVERIA SER R$ 140)

Cálculo ESPERADO (CORRETO):
├─ Consumo 01/05: R$ 100 ✓
├─ Consumo 02/05: R$ 50 ✓
├─ Lançamento 03/05: -R$ 30 ✓ INCLUIR!
├─ Consumo 04/05: R$ 20 ✓
├─ TOTAL = R$ 140 ✓
```

---

## ⚠️ Problemas Identificados

### Problema 1: Lançamentos Ignorados no Fechamento
- **Impacto**: Contas calculadas incorretamente
- **Causa**: `FechamentoContaService` considera apenas consumos
- **Exemplo**: Desconto concedido não afeta o valor final

### Problema 2: Falta de Vinculação entre Lançamento e Fechamento
- **Impacto**: Impossível auditar qual lançamento afetou qual fechamento
- **Causa**: Não há registro de qual lançamento foi incluído em qual fechamento

### Problema 3: Ordem de Cálculo Indeterminada
- **Impacto**: Lançamentos podem ser "perdidos" se vierem após fechamento
- **Causa**: Sem controle de data de corte, não há clareza sobre o período

### Problema 4: Falta de Saldo do Cliente
- **Impacto**: Impossível saber quanto o cliente já pagou vs quanto deve
- **Causa**: Não há registro de saldo acumulado

---

## 💡 Solução Proposta

### 1. Incluir Lançamentos no Cálculo de Fechamento

#### Passo 1: Buscar Lançamentos Relevantes

```java
// No FechamentoContaService.fechar()

// Buscar consumos abertos
List<Consumo> consumosAbertos = 
    consumoRepository.buscarNaoPagosPorCliente(clienteId);

// NOVO: Buscar lançamentos relevantes (pendentes/não processados)
List<Lancamento> lancamentosAbertos = 
    lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId);
    // Será necessário adicionar este método ao repositório
```

#### Passo 2: Incluir Lançamentos como Itens

```java
List<ItemFechamento> itens = new ArrayList<>();

// Consumos (código existente)
abertos.stream().map(consumo -> { ... }).forEach(itens::add);

// NOVO: Lançamentos como itens especiais
lancamentosAbertos.stream().map(lancamento -> {
    BigDecimal valor = lancamento.getNatureza() == DEBITO 
        ? lancamento.getValor()  // Positivo para débito
        : lancamento.getValor().negate();  // Negativo para crédito
    
    return ItemFechamento.builder()
        .tipoItem("LANCAMENTO")  // Diferencia de consumos
        .descricao(lancamento.getCategoria() + ": " + lancamento.getMotivo())
        .valorTotal(valor)
        .lancamentoId(lancamento.getId())
        .build();
}).forEach(itens::add);

// Somar total INCLUINDO lançamentos
BigDecimal total = itens.stream()
    .map(ItemFechamento::getValorTotal)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

#### Passo 3: Marcar Lançamentos como Processados

```java
// NOVO: Após criar fechamento
if (!lancamentosAbertos.isEmpty()) {
    lancamentoRepository.marcarLancamentosComoProcessados(
        lancamentosAbertos.stream()
            .map(Lancamento::getId)
            .collect(Collectors.toList()),
        fechamento.getId()  // Vincular ao fechamento
    );
}
```

### 2. Adicionar Campos ao ItemFechamento

```java
class ItemFechamento {
    // Campos existentes
    Long produtoId;
    String nomeProduto;
    Integer quantidade;
    BigDecimal valorUnitario;
    BigDecimal valorTotal;
    
    // NOVOS campos
    String tipoItem;              // "CONSUMO" ou "LANCAMENTO"
    Long lancamentoId;            // Se for lançamento
    String descricao;             // Descrição do lançamento
    NaturezaLancamento natureza;  // DEBITO ou CREDITO
}
```

### 3. Adicionar Métodos aos Repositórios

#### LancamentoRepository

```java
interface LancamentoRepository {
    // Existentes
    Lancamento salvar(Lancamento lancamento);
    List<Lancamento> buscarPorCliente(Long clienteId);
    
    // NOVOS
    
    // Buscar lançamentos que ainda não foram incluídos em fechamento
    List<Lancamento> buscarNaoProcessadosPorCliente(Long clienteId);
    
    // Marcar lançamentos como já processados (incluídos em fechamento)
    void marcarLancamentosComoProcessados(List<Long> lancamentoIds, Long fechamentoId);
}
```

#### Adicionar Campo ao Lancamento

```java
class Lancamento {
    // Campos existentes
    Long id;
    Long clienteId;
    LocalDateTime dataHora;
    NaturezaLancamento natureza;
    CategoriaLancamento categoria;
    BigDecimal valor;
    String motivo;
    String responsavelUsername;
    Long lancamentoOrigemId;
    
    // NOVO campo
    Long fechamentoId;  // Null enquanto não foi processado
    LocalDateTime dataProcessamento;  // Quando foi incluído no fechamento
}
```

### 4. Modificar FechamentoContaService

```java
public class FechamentoContaService {
    
    private final LancamentoRepository lancamentoRepository;  // NOVO
    
    @Transactional
    public FechamentoConta fechar(Long clienteId) {
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));

        List<Consumo> consumosAbertos = consumoRepository.buscarNaoPagosPorCliente(clienteId);
        List<Lancamento> lancamentosAbertos = lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId);  // NOVO
        
        if (consumosAbertos.isEmpty() && lancamentosAbertos.isEmpty()) {  // NOVO: verifica ambos
            throw new IllegalStateException("Cliente não possui itens em aberto");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "sistema";

        List<ItemFechamento> itens = new ArrayList<>();

        // Itens de consumos (código existente, sem mudanças)
        consumosAbertos.stream().map(consumo -> {
            Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                    .orElseThrow(() -> new ProdutoNaoEncontradoException(consumo.getDadosProduto().getProdutoId()));

            BigDecimal valorUnitario = calcularValorUnitario(cliente, consumo, produto);
            BigDecimal valorTotal = valorUnitario.multiply(
                    BigDecimal.valueOf(consumo.getDadosProduto().getQuantidade()));

            return ItemFechamento.builder()
                    .tipoItem("CONSUMO")  // NOVO: identificar tipo
                    .produtoId(produto.getId())
                    .nomeProduto(produto.getNome())
                    .quantidade(consumo.getDadosProduto().getQuantidade())
                    .valorUnitario(valorUnitario)
                    .valorTotal(valorTotal)
                    .build();
        }).forEach(itens::add);

        // NOVO: Itens de lançamentos
        lancamentosAbertos.stream().map(lancamento -> {
            BigDecimal valor = lancamento.getNatureza() == NaturezaLancamento.DEBITO
                    ? lancamento.getValor()
                    : lancamento.getValor().negate();

            return ItemFechamento.builder()
                    .tipoItem("LANCAMENTO")
                    .lancamentoId(lancamento.getId())
                    .descricao(lancamento.getCategoria() + ": " + lancamento.getMotivo())
                    .valorTotal(valor)
                    .natureza(lancamento.getNatureza())
                    .build();
        }).forEach(itens::add);

        BigDecimal total = itens.stream()
                .map(ItemFechamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FechamentoConta fechamento = FechamentoConta.builder()
                .clienteId(cliente.getId())
                .clienteNome(cliente.getNome())
                .usuarioUsername(username)
                .dataHora(LocalDateTime.now())
                .totalPago(total)
                .itens(itens)
                .build();

        FechamentoConta salvo = fechamentoRepository.salvar(fechamento);

        consumoRepository.marcarConsumosComoPagos(clienteId);  // Existente
        lancamentoRepository.marcarLancamentosComoProcessados(  // NOVO
            lancamentosAbertos.stream()
                .map(Lancamento::getId)
                .collect(Collectors.toList()),
            salvo.getId()
        );

        return salvo;
    }
}
```

---

## 📊 Exemplo de Resultado Final

### Antes da Solução

```
FECHAMENTO #1234 - Cliente: João Silva
Data: 05/05/2026
Itens:
  - Produto A: 2 × R$ 10 = R$ 20
  - Produto B: 1 × R$ 30 = R$ 30
  - Produto C: 3 × R$ 15 = R$ 45
  
TOTAL: R$ 95 ❌ (Desconto de R$ 30 não foi considerado!)
```

### Depois da Solução

```
FECHAMENTO #1234 - Cliente: João Silva
Data: 05/05/2026
Itens:
  - [CONSUMO] Produto A: 2 × R$ 10 = R$ 20
  - [CONSUMO] Produto B: 1 × R$ 30 = R$ 30
  - [CONSUMO] Produto C: 3 × R$ 15 = R$ 45
  - [LANCAMENTO] DESCONTO: Fidelidade = -R$ 30
  
TOTAL: R$ 65 ✓ (Correto!)
```

---

## 🔍 Implementação em Etapas

### Etapa 1: Preparar Banco de Dados
- [ ] Adicionar coluna `fechamento_id` na tabela `lancamento`
- [ ] Adicionar coluna `data_processamento` na tabela `lancamento`
- [ ] Criar índice em `(cliente_id, fechamento_id)`

### Etapa 2: Atualizar Entidades JPA
- [ ] Adicionar campos ao `LancamentoEntity`
- [ ] Criar migration do Flyway

### Etapa 3: Implementar Repositório
- [ ] Método `buscarNaoProcessadosPorCliente()`
- [ ] Método `marcarLancamentosComoProcessados()`

### Etapa 4: Refatorar FechamentoContaService
- [ ] Incluir lógica de lançamentos
- [ ] Testar com múltiplos cenários

### Etapa 5: Atualizar DTOs
- [ ] Adicionar campos em `ItemFechamento`
- [ ] Atualizar `FechamentoResponseDTO`

### Etapa 6: Testes
- [ ] Testes unitários do serviço
- [ ] Testes de integração
- [ ] Testes com múltiplos lançamentos e consumos

---

## 📚 Referências

- **Consumo**: Registra produtos/serviços consumidos
- **Lançamento**: Ajustes e movimentações na conta
- **Fechamento**: Consolidação periódica dos valores devidos

Todos os três módulos devem trabalhar **em conjunto** para calcular corretamente o valor final da conta do cliente.

