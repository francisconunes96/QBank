package com.totvs.tj.qbank.domain.movimentacao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class CompraDivida {

    private CompraDividaId id;
    private Transferencia transferencia;
    private Situacao situacao;

    public static enum Situacao {
	INICIADA, RECUSADA, APROVADA
    }

    public static CompraDivida from(Transferencia transferencia) {
	return CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.INICIADA);
    }

    public boolean efetuar() {
	return transferencia.transferir();
    }

    public void aprovar() {
	this.situacao = Situacao.APROVADA;
    }

    public void recusar() {
	this.situacao = Situacao.RECUSADA;
    }
}
