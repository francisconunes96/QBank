package com.totvs.tj.qbank.domain.movimentacao;

import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity
public class Emprestimo {
    
    @EmbeddedId
    private final EmprestimoId id;
    @OneToOne
    private final Movimento movimento;
    private Situacao situacao;

    public static Builder builder() {
        return new Builder();
    }

    public Emprestimo(Builder builder) {
        this.id = builder.id;
        this.movimento = builder.movimento;
        this.situacao = builder.situacao;
    }

    public void emprestar() {		
		movimento.processar();
		liberar();
	}

    public void liberar() {
        this.getMovimento().aprovar();
        this.situacao = Situacao.LIBERADO;
    }

    public void aguardarAprovacao() {
        this.situacao = Situacao.AGUARDANDO_APROVACAO;
    }

    public void recusar() {
        this.getMovimento().recusar();
        this.situacao = Situacao.RECUSADO;
    }

    public boolean quitar() {
        this.situacao = Situacao.QUITADO;
        return true;
    }

    public BigDecimal getValor() {
        return this.movimento.getValor();
    }

    public static class Builder {

        private EmprestimoId id;
        private Movimento movimento;
        private Situacao situacao;

        public Builder id(EmprestimoId id) {
            this.id = id;
            return this;
        }

        public Builder movimento(Movimento movimento) {
            this.movimento = movimento;
            return this;
        }

        public Emprestimo build() {
            this.situacao = Situacao.PENDENTE;
            return new Emprestimo(this);
        }

    }

    public static enum Situacao {
        PENDENTE,
        AGUARDANDO_APROVACAO,
        LIBERADO,
        RECUSADO,
        QUITADO
    }

}
