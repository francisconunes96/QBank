package com.totvs.tj.qbank.domain.documento;

public class CNPJ {
    
    private String numero;
    
    public CNPJ(String numero) {
        this.numero = numero;
    }

    public static CNPJ of(String numero) {
        return new CNPJ(numero);
    }
    
}
