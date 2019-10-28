package com.totvs.tj.qbank.domain.empresa;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.documento.CNPJ;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Empresa {
    
    private ResponsavelId responsavel;
    private EmpresaId id;
    private String nome;
    private CNPJ cnpj;
    private BigDecimal valorMercado;
    private Integer quantidadeFuncionarios;
    
}
