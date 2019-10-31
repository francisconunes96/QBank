package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.CompraDivida;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitarAprovacaoCompra {
    private CompraDivida compraDivida;
    private Situacao situacao;

    public static enum Situacao {
	INICIADA, APROVADA
    }

    public boolean isAprovada() {
	return Situacao.APROVADA.equals(this.situacao);
    }
}
