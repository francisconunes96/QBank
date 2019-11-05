package com.totvs.tj.qbank.domain.empresa;

import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Entity
public class Empresa {
    
    @EmbeddedId
	private EmpresaId id;
	private String cnpj;
    private String responsavel;
    private Integer quantidadeFuncionarios;
    private BigDecimal valorMercado;
    
}
