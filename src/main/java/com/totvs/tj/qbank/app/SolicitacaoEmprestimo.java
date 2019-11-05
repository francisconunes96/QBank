package com.totvs.tj.qbank.app;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.conta.Conta;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitacaoEmprestimo {
	
	private BigDecimal valor;
	private Conta conta;
	
	
}
