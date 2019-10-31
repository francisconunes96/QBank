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
	INICIADA
    }

    public static CompraDivida from(Transferencia transferencia) {
	return CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.INICIADA);
    }

    public boolean efetuar() {
	return transferencia.transferir();
    }
}
