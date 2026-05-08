package com.megamanager.lancamento.domain;

/**
 * Categoria do lançamento para fins de auditoria e relatórios.
 *
 * A natureza (DEBITO/CREDITO) é independente da categoria.
 */
public enum CategoriaLancamento {
    PAGAMENTO,
    SERVICO,
    DESCONTO,
    CORRECAO,
    COBRANCA_ADICIONAL,
    ESTORNO
}
