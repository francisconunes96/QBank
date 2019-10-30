package com.totvs.tj.qbank.domain.movimentacao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Transferencia {

    private final TransferenciaId id;
    private final Movimento credito;
    private final Movimento debito;
    private Situacao situacao;

    public static Builder builder() {
        return new Builder();
    }

    public Transferencia(Builder builder) {
        this.id = builder.id;
        this.credito = builder.credito;
        this.debito = builder.debito;
        this.situacao = Situacao.ABERTA;
    }

    public static class Builder {

        private TransferenciaId id;
        private Movimento credito;
        private Movimento debito;

        public Builder id(TransferenciaId id) {
            this.id = id;
            return this;
        }

        public Builder credito(Movimento credito) {
            this.credito = credito;
            return this;
        }

        public Builder debito(Movimento debito) {
            this.debito = debito;
            return this;
        }

        public Transferencia build() {
            return new Transferencia(this);
        }

    }

    public static enum Situacao {
        ABERTA,
        FINALIZADA
    }

}
