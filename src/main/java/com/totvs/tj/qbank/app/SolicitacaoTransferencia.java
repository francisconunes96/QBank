package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Transferencia;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitacaoTransferencia {
	
	private Transferencia transferencia;
	
}
