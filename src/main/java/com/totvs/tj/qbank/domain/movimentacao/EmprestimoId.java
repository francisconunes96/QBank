package com.totvs.tj.qbank.domain.movimentacao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName="from")
public class EmprestimoId {
	
	private String value;
	
	public static EmprestimoId generate() {
		return EmprestimoId.from(UUID.randomUUID().toString());
	}
	
}
