package com.totvs.tj.qbank.domain.empresa;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Empresa {
    
	private EmpresaId id;
	private String cnpj;
    private String responsavel;
    private Integer quantidadeFuncionarios;
    private BigDecimal valorMercado;
    
}
