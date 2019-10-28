package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.conta.ContaId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitacaoAumentoLimiteEmergencial {

    private ContaId idConta ;
    
}
