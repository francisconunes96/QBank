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
    private BigDecimal saldo;
    private BigDecimal limite;
    private BigDecimal limiteEmergencial;
    private Situacao situacao;
    
    public boolean aumentarLimite() {
        
        if (this.limiteEmergencial.compareTo(BigDecimal.ZERO) == 1) {
            return false;
        }
        
        this.limiteEmergencial = this.limite.divide(BigDecimal.valueOf(2));
        
        return true;
    }
    
    public void suspender() {
        
        this.situacao = Situacao.SUSPENSA;
        
    }
    
    public boolean isDisponivel() {
        return Situacao.DISPONIVEL.equals(this.situacao);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private ContaId id;
        private Empresa empresa;
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
            return new Conta(id, empresa.getId(), BigDecimal.ZERO, limite, BigDecimal.ZERO, Situacao.DISPONIVEL);
        }       
        
    }
    
    public static enum Situacao {
        DISPONIVEL,
        SUSPENSA
    }
}
