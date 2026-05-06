# 📊 Diagramas Visuais: Fluxo de Lançamento, Consumo e Fechamento

## 1. Arquitetura Atual (Com Problema) ❌

```
┌─────────────────────────────────────────────────────────────────────┐
│                     MÓDULO LANÇAMENTO                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  Lançamento (Débito/Crédito)                                        │
│  ├─ id: 100                                                          │
│  ├─ clienteId: 1                                                     │
│  ├─ natureza: CREDITO                                                │
│  ├─ categoria: DESCONTO                                              │
│  ├─ valor: R$ 30                                                     │
│  └─ fechamento_id: NULL ← PROBLEMA!                                 │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
                              ↓ SEPARADO
                              ↓
┌─────────────────────────────────────────────────────────────────────┐
│                     MÓDULO CONSUMO                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  Consumo 1: R$ 100 (pago=false)                                     │
│  Consumo 2: R$ 50 (pago=false)                                      │
│  Consumo 3: R$ 20 (pago=false)                                      │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
                              ↓
                              ↓ Buscar apenas consumos
                              ↓
┌─────────────────────────────────────────────────────────────────────┐
│                   MÓDULO FECHAMENTO                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  Fechamento #1234                                                    │
│  ├─ Consumo 1: R$ 100                                               │
│  ├─ Consumo 2: R$ 50                                                │
│  ├─ Consumo 3: R$ 20                                                │
│  ├─ TOTAL: R$ 170 ❌ (Ignora desconto de R$ 30!)                   │
│  └─ Lançamento: IGNORADO! ← BUG!                                   │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. Arquitetura Corrigida (Com Solução) ✅

```
┌─────────────────────────────────────────────────────────────────────┐
│                     MÓDULO LANÇAMENTO                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  Lançamento (Débito/Crédito)                                        │
│  ├─ id: 100                                                          │
│  ├─ clienteId: 1                                                     │
│  ├─ natureza: CREDITO                                                │
│  ├─ categoria: DESCONTO                                              │
│  ├─ valor: R$ 30                                                     │
│  ├─ fechamento_id: NULL ✓ (Será preenchido no fechamento)           │
│  └─ data_processamento: NULL ✓ (Será preenchido no fechamento)      │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
           ↘                                                    ↙
            ↘                                                  ↙
             ↘                                              ↙
              ↘ Busca de dados integrada ↙
               ↘                      ↙
┌─────────────────────────────────────────────────────────────────────┐
│                     MÓDULO CONSUMO                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  Consumo 1: R$ 100 (pago=false)                                     │
│  Consumo 2: R$ 50 (pago=false)                                      │
│  Consumo 3: R$ 20 (pago=false)                                      │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
           ↘                                              ↙
            ↘                                          ↙
             ↘                                      ↙
              ↘ Busca integrada ↙
               ↘               ↙
┌─────────────────────────────────────────────────────────────────────┐
│                   MÓDULO FECHAMENTO                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  Fechamento #1234                                                    │
│  ├─ [CONSUMO] Consumo 1: R$ 100 ✓                                   │
│  ├─ [CONSUMO] Consumo 2: R$ 50 ✓                                    │
│  ├─ [CONSUMO] Consumo 3: R$ 20 ✓                                    │
│  ├─ [LANCAMENTO] DESCONTO: -R$ 30 ✓ INCLUÍDO!                      │
│  ├─ TOTAL: R$ 140 ✓ (CORRETO!)                                     │
│  └─ Marca consumos como pagos                                       │
│     └─ Marca lançamentos como processados (fechamento_id=1234)      │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Fluxo de Dados no Fechamento (Passo a Passo)

```
CLIENTE: João Silva
DATA: 05/05/2026

PASSO 1: Buscar Consumos Não Pagos
┌──────────────────────────────────┐
│ SELECT * FROM consumo             │
│ WHERE cliente_id = 1              │
│ AND pago = false                  │
│ ORDER BY data_hora ASC            │
│                                   │
│ Resultado:                        │
│ ├─ Consumo 1: R$ 100             │
│ ├─ Consumo 2: R$ 50              │
│ └─ Consumo 3: R$ 20              │
└──────────────────────────────────┘
         ↓
PASSO 2: Buscar Lançamentos Não Processados [NOVO]
┌──────────────────────────────────┐
│ SELECT * FROM lancamento          │
│ WHERE cliente_id = 1              │
│ AND fechamento_id IS NULL         │
│ ORDER BY data_hora ASC            │
│                                   │
│ Resultado:                        │
│ ├─ Lançamento 1: R$ 30 (CREDITO)  │
│ └─ Lançamento 2: R$ 10 (DEBITO)   │
└──────────────────────────────────┘
         ↓
PASSO 3: Processar Itens
┌──────────────────────────────────────────────────────┐
│ Para cada CONSUMO:                                   │
│   ├─ Buscar Produto                                  │
│   ├─ Calcular Valor (baseado no perfil)             │
│   └─ Criar ItemFechamento com tipo='CONSUMO'        │
│                                                       │
│ Para cada LANÇAMENTO: [NOVO]                        │
│   ├─ Se natureza == DEBITO: valor positivo          │
│   ├─ Se natureza == CREDITO: valor negativo         │
│   └─ Criar ItemFechamento com tipo='LANCAMENTO'     │
│                                                       │
│ Itens criados:                                       │
│ ├─ [CONSUMO] Produto A × 2 = R$ 100               │
│ ├─ [CONSUMO] Produto B × 1 = R$ 50                │
│ ├─ [CONSUMO] Produto C × 3 = R$ 20                │
│ ├─ [LANCAMENTO] DESCONTO = -R$ 30                 │
│ └─ [LANCAMENTO] TAXA = +R$ 10                      │
└──────────────────────────────────────────────────────┘
         ↓
PASSO 4: Calcular Total
┌──────────────────────────────────────────────────────┐
│ totalPago = SUM(valorTotal)                          │
│                                                       │
│ 100 + 50 + 20 - 30 + 10 = 150                       │
│                                                       │
│ Antes: 170 ❌                                         │
│ Depois: 150 ✓                                        │
└──────────────────────────────────────────────────────┘
         ↓
PASSO 5: Salvar Fechamento
┌──────────────────────────────────────────────────────┐
│ INSERT INTO fechamento_conta (                       │
│   cliente_id, cliente_nome,                          │
│   usuario_username, data_hora,                       │
│   total_pago, itens                                  │
│ ) VALUES (...)                                       │
│                                                       │
│ fechamento_id = 1234                                 │
└──────────────────────────────────────────────────────┘
         ↓
PASSO 6: Marcar Consumos como Pagos
┌──────────────────────────────────────────────────────┐
│ UPDATE consumo                                       │
│ SET pago = true                                      │
│ WHERE cliente_id = 1 AND pago = false               │
│                                                       │
│ Atualizado: 3 consumos                               │
└──────────────────────────────────────────────────────┘
         ↓
PASSO 7: Marcar Lançamentos como Processados [NOVO]
┌──────────────────────────────────────────────────────┐
│ UPDATE lancamento                                    │
│ SET fechamento_id = 1234,                            │
│     data_processamento = NOW()                       │
│ WHERE id IN (1, 2)                                   │
│                                                       │
│ Atualizado: 2 lançamentos                            │
│ ├─ Lançamento 1: fechamento_id = 1234               │
│ └─ Lançamento 2: fechamento_id = 1234               │
└──────────────────────────────────────────────────────┘
```

---

## 4. Diagrama de Estado do Lançamento

```
┌─────────────────────────────┐
│   LANÇAMENTO CRIADO         │
│                             │
│ fechamento_id = NULL        │
│ data_processamento = NULL   │
│                             │
│ Estado: "PENDENTE"          │
└──────────────────┬──────────┘
                   │
                   │ Quando FechamentoContaService.fechar() é chamado
                   │
                   ↓
┌─────────────────────────────┐
│ LANÇAMENTO PROCESSADO       │
│                             │
│ fechamento_id = 1234        │
│ data_processamento = 2026.. │
│                             │
│ Estado: "INCLUÍDO EM FCT"   │
└─────────────────────────────┘
```

---

## 5. Estados dos Itens do Fechamento

```
CONSUMO
├─ pago = false  ──[Fechamento]──> pago = true
├─ ItemFechamento.tipoItem = 'CONSUMO'
└─ ItemFechamento.produtoId = preenchido

LANÇAMENTO [NOVO]
├─ fechamento_id = NULL  ──[Fechamento]──> fechamento_id = 1234
├─ data_processamento = NULL  ──[Fechamento]──> data_processamento = 2026-05-04 15:30:00
├─ ItemFechamento.tipoItem = 'LANCAMENTO'
└─ ItemFechamento.lancamentoId = preenchido
```

---

## 6. Matriz de Tipos de Lançamento

```
┌─────────────────────┬────────────┬─────────────────────┬──────────────┐
│ Categoria           │ Natureza   │ Efetiva no Totał    │ Motivo       │
├─────────────────────┼────────────┼─────────────────────┼──────────────┤
│ PAGAMENTO           │ CREDITO    │ -valor              │ Pagto recb   │
│ DESCONTO            │ CREDITO    │ -valor              │ Desconto     │
│ COBRANCA_ADICIONAL  │ DEBITO     │ +valor              │ Taxa/Multa   │
│ CORRECAO            │ DEBITO     │ +valor              │ Correção     │
│ CORRECAO            │ CREDITO    │ -valor              │ Ajuste       │
│ SERVICO             │ DEBITO     │ +valor              │ Serviço      │
│ ESTORNO             │ DEBITO*    │ +valor*             │ Reversão     │
│ ESTORNO             │ CREDITO*   │ -valor*             │ Reversão     │
└─────────────────────┴────────────┴─────────────────────┴──────────────┘

* ESTORNO sempre inverte a natureza do lançamento original
```

---

## 7. Fluxo Completo de Um Caso Real

```
CENÁRIO: Cliente João Silva - Período: 01/05 a 05/05

LINHA DO TEMPO:
┌─────────────────────────────────────────────────────────────────┐
│                                                                   │
│ 01/05 │ Consumo Produto A: 2 × R$ 10 = R$ 20 (pago=false)       │
│       └─ Salvo em consumo                                        │
│                                                                   │
│ 02/05 │ Consumo Produto B: 1 × R$ 30 = R$ 30 (pago=false)       │
│       └─ Salvo em consumo                                        │
│                                                                   │
│ 03/05 │ Lançamento: DESCONTO - R$ 10 (CREDITO)                  │
│       └─ Motivo: "Fidelidade"                                    │
│       └─ Salvo em lancamento com fechamento_id=NULL             │
│                                                                   │
│ 04/05 │ Consumo Produto C: 3 × R$ 15 = R$ 45 (pago=false)       │
│       └─ Salvo em consumo                                        │
│                                                                   │
│ 05/05 │ FECHAR CONTA do cliente 1                                │
│       │                                                           │
│       └─ Buscar consumos não pagos:                              │
│           ├─ Consumo A: R$ 20                                    │
│           ├─ Consumo B: R$ 30                                    │
│           └─ Consumo C: R$ 45                                    │
│                                                                   │
│       └─ Buscar lançamentos não processados:                     │
│           └─ Lançamento DESCONTO: -R$ 10 (CREDITO)             │
│                                                                   │
│       └─ Criar FechamentoConta:                                  │
│           ├─ [CONSUMO] Produto A: 2 × R$ 10 = R$ 20            │
│           ├─ [CONSUMO] Produto B: 1 × R$ 30 = R$ 30            │
│           ├─ [CONSUMO] Produto C: 3 × R$ 15 = R$ 45            │
│           ├─ [LANCAMENTO] DESCONTO: -R$ 10                     │
│           ├─ TOTAL: R$ 20 + R$ 30 + R$ 45 - R$ 10 = R$ 85      │
│           └─ Data do Fechamento: 05/05/2026 15:30               │
│                                                                   │
│       └─ Salvar Fechamento #5000                                 │
│                                                                   │
│       └─ Atualizar consumos:                                     │
│           ├─ UPDATE consumo SET pago=true WHERE...              │
│           └─ 3 consumos marcados como pagos                      │
│                                                                   │
│       └─ Atualizar lançamentos:                                  │
│           ├─ UPDATE lancamento SET fechamento_id=5000,          │
│           │         data_processamento='05/05/2026 15:30'       │
│           └─ 1 lançamento marcado como processado               │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘

RESULTADO FINAL:
┌────────────────────────────────────────┐
│ CONTA DO CLIENTE - FECHAMENTO #5000    │
├────────────────────────────────────────┤
│ Cliente: João Silva                    │
│ Data: 05/05/2026 15:30                 │
│ Usuario: admin                         │
│                                        │
│ ITENS:                                 │
│ ├─ [C] Produto A (2): R$ 20           │
│ ├─ [C] Produto B (1): R$ 30           │
│ ├─ [C] Produto C (3): R$ 45           │
│ ├─ [L] Desconto (Fidelidade): -R$ 10  │
│                                        │
│ TOTAL A PAGAR: R$ 85 ✓                 │
│                                        │
│ Status: Fechado                        │
└────────────────────────────────────────┘
```

---

## 8. Queries SQL Importantes

```sql
-- Verificar consumos não pagos
SELECT * FROM consumo 
WHERE cliente_id = 1 AND pago = false 
ORDER BY data_hora ASC;

-- Verificar lançamentos não processados [NOVO]
SELECT * FROM lancamento 
WHERE cliente_id = 1 AND fechamento_id IS NULL 
ORDER BY data_hora ASC;

-- Verificar fechamentos criados
SELECT * FROM fechamento_conta 
WHERE cliente_id = 1 
ORDER BY data_hora DESC;

-- Verificar lançamentos já processados [NOVO]
SELECT * FROM lancamento 
WHERE cliente_id = 1 AND fechamento_id IS NOT NULL 
ORDER BY data_processamento DESC;

-- Auditoria: qual lançamento entrou em qual fechamento [NOVO]
SELECT 
    l.id, l.categoria, l.valor, l.natureza,
    l.fechamento_id, l.data_processamento,
    f.data_hora as fechamento_data
FROM lancamento l
LEFT JOIN fechamento_conta f ON l.fechamento_id = f.id
WHERE l.cliente_id = 1
ORDER BY l.data_hora;
```

---

## 9. Impacto das Mudanças

### Antes ❌
```
Total = SUM(consumo.valor_total)
└─ Lançamentos completamente ignorados
└─ Contas incorretas
└─ Impossível auditar
```

### Depois ✓
```
Total = SUM(consumo.valor_total) + SUM(lançamento.valor_assinado)
├─ Lançamentos incluídos no cálculo
├─ Contas corretas
├─ Rastreabilidade completa (fechamento_id, data_processamento)
└─ Auditoria total do que foi incluído em cada fechamento
```


