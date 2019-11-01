package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Emprestimo;

public class EmprestimoService {
		
	public Emprestimo handle(SolicitacaoEmprestimo cmd) {

		Emprestimo emprestimo = cmd.getEmprestimo();

		SolicitacaoVerificacaoSaldo verificacaoSaldo = SolicitacaoVerificacaoSaldo.of(emprestimo.getMovimento());
		
		VerificacaoLimiteService service = new VerificacaoLimiteService();

		ResultadoVerificacaoSaldo resultado = service.handle(verificacaoSaldo);

		if (SaldoExcedido.class.equals(resultado.getClass())) {
			emprestimo.aguardarAprovacao();
			return emprestimo;
		}

		if (emprestimo.emprestar()) {
			emprestimo.liberar();
		}

		return emprestimo;
	}

	public Emprestimo handle(SolicitacaoAprovacaoEmprestimo cmd) {

		Emprestimo emprestimo = cmd.getEmprestimo();

		if (cmd.isAprovada() && emprestimo.emprestar()) {
			emprestimo.liberar();			
		} else {
			emprestimo.recusar();
		}

		return emprestimo;
	}
	
}
