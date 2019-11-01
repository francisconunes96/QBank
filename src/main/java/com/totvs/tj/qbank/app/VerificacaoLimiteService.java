package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;

public class VerificacaoLimiteService {
		
	public ResultadoVerificacaoSaldo handle(SolicitacaoVerificacaoSaldo cmd) {
		Movimento movimento = cmd.getMovimento();
		Conta conta = movimento.getConta();

		if (conta.estaDentroDoLimite(movimento.getValor())) {
			return SaldoDentroLimite.from(movimento);
		}

		return SaldoExcedido.from(movimento);
	}
	
}
