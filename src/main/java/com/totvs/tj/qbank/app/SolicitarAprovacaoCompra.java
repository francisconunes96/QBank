package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.CompraDividaId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitarAprovacaoCompra {
    
    private CompraDividaId compraDividaId;
    private Situacao situacao;

    public static enum Situacao {
        INICIADA,
        APROVADA,
        RECUSADA
    }

    public boolean isAprovada() {
        return Situacao.APROVADA.equals(this.situacao);
    }
}
