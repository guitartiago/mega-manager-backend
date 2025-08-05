package com.megamanager.cliente.domain;

public enum PerfilCliente {
    COMUM(false, false),
    SOCIO(true, true),
    PARCEIRO(true, false);

    private final boolean temBeneficioProduto;
    private final boolean temBeneficioEnsaio;

    PerfilCliente(boolean temBeneficioProduto, boolean temBeneficioEnsaio) {
        this.temBeneficioProduto = temBeneficioProduto;
        this.temBeneficioEnsaio = temBeneficioEnsaio;
    }

    public boolean temBeneficioProduto() {
        return temBeneficioProduto;
    }
    
    public boolean temBeneficioProdutoEnsaio() {
        return temBeneficioEnsaio;
    }
}
