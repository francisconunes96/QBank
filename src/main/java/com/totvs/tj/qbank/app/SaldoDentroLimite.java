package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Movimento;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SaldoDentroLimite implements ResultadoVerificacaoSaldo {
    
    private Movimento movimento;    
}
