package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.empresa.Empresa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SolicitacaoAberturaConta {
    
    private Empresa empresa;    
    
}
