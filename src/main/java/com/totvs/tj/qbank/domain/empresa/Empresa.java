package com.totvs.tj.qbank.domain.empresa;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Empresa {
    
    private String responsavel;
    private EmpresaId id;
    private String nome;
    private String cnpj;
    private BigDecimal valorMercado;
    private Integer quantidadeFuncionarios;
    
}
