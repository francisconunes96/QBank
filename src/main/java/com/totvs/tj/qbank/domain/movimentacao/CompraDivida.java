package com.totvs.tj.qbank.domain.movimentacao;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE, force = true)
@AllArgsConstructor(staticName = "from")
@Entity
public class CompraDivida {

    @Id
    private CompraDividaId id;
    
    @OneToOne(fetch = LAZY, optional = false)
    private Transferencia transferencia;

    @Enumerated(STRING)
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

    public void efetuar() {
        transferencia.transferir();
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
