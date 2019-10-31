package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.conta.Conta;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitacaoCompraDivida {
    
    private Conta contaSolicitante;
    private Conta contaSolicitada;

}
