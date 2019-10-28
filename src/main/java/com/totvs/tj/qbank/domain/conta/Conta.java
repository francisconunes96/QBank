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
    private Situacao situacao;
    
    public boolean aumentarLimite() {
        
        this.limite = this.limite.add(this.limite.divide(BigDecimal.valueOf(2)));
        
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
            return new Conta(id, empresa.getId(), saldo, limite, Situacao.DISPONIVEL);
        }       
        
    }
    
    public static enum Situacao {
        DISPONIVEL,
        SUSPENSA
    }
}
