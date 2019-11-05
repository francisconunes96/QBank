package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName="from")
public class SolicitacaoQuitacaoDivida {
    
    private final EmprestimoId emprestimoId;

}
