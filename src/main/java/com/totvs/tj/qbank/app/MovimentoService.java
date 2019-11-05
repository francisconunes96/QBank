package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Movimento;

public class MovimentoService {
		
	public Movimento handle(SolicitacaoAprovacaoGerente cmd) {

		Movimento movimento = cmd.getMovimento();

		if (cmd.isAprovada()) {
			movimento.aprovar();
		} else {
			movimento.recusar();
		}
		
		return movimento;
	}
	
	
}
