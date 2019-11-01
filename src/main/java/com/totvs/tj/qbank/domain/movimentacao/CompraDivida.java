package com.totvs.tj.qbank.domain.movimentacao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class CompraDivida {

    private CompraDividaId id;
    private Transferencia transferencia;
    private Situacao situacao;

    public static enum Situacao {
        RECUSADA,
        APROVADA,
        AGUARDANDO_APROVACAO_SOLICITADO,
        AGUARDANDO_LIBERACAO_MOVIMENTO
    }
    
    public static CompraDivida from(Transferencia transferencia) {
        return CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.AGUARDANDO_APROVACAO_SOLICITADO);
    }

    public boolean efetuar() {
        return transferencia.transferir();
    }

    public void aprovar() {
        this.situacao = Situacao.APROVADA;
    }
    
    public void aprovarMovimento() {
        transferencia.getDebito().aprovar();
        this.situacao = Situacao.AGUARDANDO_APROVACAO_SOLICITADO;
    }
    
    public void recusarMovimento() {
        transferencia.getDebito().recusar();
        this.recusar();
    }

    public void recusar() {
        this.situacao = Situacao.RECUSADA;
    }

    public static CompraDivida solicitarAprovacaoMovimento(Transferencia transferencia) {
        transferencia.getDebito().solicitarAprovacao();
        return CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.AGUARDANDO_LIBERACAO_MOVIMENTO);
    }

    public Movimento.Situacao getSituacaoDebito() {
        return transferencia.getDebito().getSituacao();
    }

}
