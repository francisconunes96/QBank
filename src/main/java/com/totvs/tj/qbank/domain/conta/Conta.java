package com.totvs.tj.qbank.domain.conta;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Conta {
    
    private final ContaId id;
    private final EmpresaId empresa;
    private final BigDecimal saldo;
    private final BigDecimal limite;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private ContaId id;
        private Empresa empresa;
        private BigDecimal saldo = BigDecimal.ZERO;
        private BigDecimal limite;
        
        public Builder id(ContaId id) {
            this.id = id;
            return this;
        }
        
        public Builder empresa(Empresa empresa) {
            this.empresa = empresa;
            return this;
        }
        
        public Builder calcularLimite() {
            this.limite = this.empresa.getValorMercado().divide(BigDecimal.valueOf(empresa.getQuantidadeFuncionarios()));
            
            if (this.limite.compareTo(BigDecimal.valueOf(15000)) == 1) {
                this.limite = BigDecimal.valueOf(15000);
            }
            
            return this;
        }
        
        public Conta build() {                       
            return new Conta(id, empresa.getId(), saldo, limite);
        }       
        
    }
    
    
}
