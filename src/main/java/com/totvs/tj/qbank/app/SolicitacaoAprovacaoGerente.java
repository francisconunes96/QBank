package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Movimento;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class SolicitacaoAprovacaoGerente {

    private ResultadoVerificacaoSaldo saldoExcedido;
    private Situacao situacao;
    
    public static enum Situacao {
        APROVADA, RECUSADA
    }
    
    public boolean isAprovada() {
        return SolicitacaoAprovacaoGerente.Situacao.APROVADA.equals(this.getSituacao());
    }
    
    public  Movimento getMovimento() {
        return saldoExcedido.getMovimento();
    }
}
