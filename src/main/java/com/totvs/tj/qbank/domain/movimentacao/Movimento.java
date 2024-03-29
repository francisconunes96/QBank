package com.totvs.tj.qbank.domain.movimentacao;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.totvs.tj.qbank.domain.conta.Conta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE, force = true)
@AllArgsConstructor
@Entity
public class Movimento {
    
    @EmbeddedId
    private final MovimentoId id;
    private final BigDecimal valor;
    
    @ManyToOne
    private final Conta conta;
    
    @Enumerated(STRING)
    private final Tipo tipo;
    
    @Enumerated(STRING)
    private final Origem origem;
    
    @Enumerated(STRING)
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
        this.origem = builder.origem;
    }

    public void aprovar() {
        this.situacao = Situacao.APROVADO;
    }

    public void recusar() {
        this.situacao = Situacao.RECUSADO;
    }

    public void finalizar() {
        this.situacao = Situacao.FINALIZADO;
    }
    
    public void solicitarAprovacao() {
        this.situacao = Situacao.AGUARDANDO_APROVACAO;
    }

    public boolean isAprovado() {
        return Situacao.APROVADO.equals(this.getSituacao());
    }

    public boolean isRecusado() {
        return Situacao.RECUSADO.equals(this.getSituacao());
    }
    
    public void processar() {
        if (Tipo.CREDITO.equals(this.tipo)) {
            this.conta.creditar(this.valor);
        } else {
            this.conta.debitar(this.valor);
        }
    }

    public static class Builder {

        private MovimentoId id;
        private BigDecimal valor;
        private Conta conta;
        private Tipo tipo;
        private Situacao situacao;
        private Origem origem;

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

        public Builder debito() {
            this.tipo = Tipo.DEBITO;
            return this;
        }

        public Builder credito() {
            this.tipo = Tipo.CREDITO;
            return this;
        }
        
        public Builder emprestimo() {
            this.origem = Origem.EMPRESTIMO;
            return this;
        }
        
        public Builder compraDivida() {
            this.origem = Origem.COMPRA_DIVIDA;
            return this;
        }

        public Movimento build() {
            this.situacao = Situacao.ABERTO;
            return new Movimento(this);
        }
    }

    public static enum Situacao {
        ABERTO,
        APROVADO,
        RECUSADO,
        FINALIZADO,
        AGUARDANDO_APROVACAO
    }

    private static enum Tipo {
        DEBITO,
        CREDITO
    }
    
    private static enum Origem {
        EMPRESTIMO,
        COMPRA_DIVIDA
    }

}
