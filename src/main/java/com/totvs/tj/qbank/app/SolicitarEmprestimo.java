package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Emprestimo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitarEmprestimo {
	
	private Emprestimo emprestimo;	
}
