package com.totvs.tj.qbank.domain.movimentacao;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.conta.Conta;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Movimento {
        
    private final MovimentoId id;
    private final BigDecimal valor;
    private final Conta conta;    
    private final Tipo tipo;
    private Situacao situacao;
               
    public static Builder builder() {
        return new Builder();
    }
    
    private Movimento(Builder builder) {
       this.id = builder.id;
       this.valor = builder.valor;
       this.conta = builder.conta;
       this.tipo = builder.tipo;
       this.situacao = builder.situacao;
    }
    
    public void aprovar() {
        this.situacao = Situacao.APROVADO;
    }
    
    public boolean isAprovado() {
        return Situacao.APROVADO.equals(this.getSituacao());
    }
        
    public static class Builder {
        
        private MovimentoId id;
        private BigDecimal valor;
        private Conta conta;
        private Tipo tipo;
        private Situacao situacao;
        
        public Builder id(MovimentoId id) {
            this.id = id;
            return this;
        }
        
        public Builder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }
        
        public Builder conta(Conta conta) {
            this.conta = conta;
            return this;
        }
        
        public Builder tipoSaida() {
            this.tipo = Tipo.SAIDA;
            return this;
        }
        
        public Movimento build() {
            this.situacao = Situacao.ABERTO;
            return new Movimento(this);
        }        
    }
    
    public static enum Situacao {
        ABERTO, APROVADO, RECUSADO
    }
    
    private static enum Tipo {
        SAIDA, ENTRADA
    }
    
}
