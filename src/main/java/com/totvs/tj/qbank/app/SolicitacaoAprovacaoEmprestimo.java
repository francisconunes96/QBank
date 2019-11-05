package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName="from")
public class SolicitacaoAprovacaoEmprestimo {
	
	private final EmprestimoId emprestimoId;
	private final Situacao situacao;
	
	public static enum Situacao {
		APROVADA, RECUSADA
	}
	
	public boolean isAprovada() {
		return Situacao.APROVADA.equals(this.situacao);
	}
	
}
