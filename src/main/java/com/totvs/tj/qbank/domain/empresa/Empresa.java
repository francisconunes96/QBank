package com.totvs.tj.qbank.domain.empresa;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE, force = true)
@AllArgsConstructor
@Builder
@Entity
public class Empresa {

    @Id
    private EmpresaId id;
    private String cnpj;
    private String responsavel;
    private Integer quantidadeFuncionarios;
    private BigDecimal valorMercado;

}
