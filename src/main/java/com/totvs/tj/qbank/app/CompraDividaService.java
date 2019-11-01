package com.totvs.tj.qbank.app;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.Transferencia;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;

public class CompraDividaService {

    public Transferencia handle(SolicitacaoTransferencia cmd) {

        Transferencia transferencia = cmd.getTransferencia();

       transferencia.transferir();
       
        return transferencia;
    }

    public CompraDivida handle(SolicitacaoCompraDivida cmd) {

        Conta solicitada = cmd.getContaSolicitada();
        Conta solicitante = cmd.getContaSolicitante();

        BigDecimal valorMovimento = solicitada.getSaldo().negate();

        Movimento movimentoSaida = Movimento.builder().id(MovimentoId.generate()).tipoSaida().conta(solicitante)
                .valor(valorMovimento).build();

        Movimento movimentoEntrada = Movimento.builder().id(MovimentoId.generate()).tipoEntrada().conta(solicitada)
                .valor(valorMovimento).build();

        Transferencia transferencia = Transferencia.builder().id(TransferenciaId.generate()).credito(movimentoEntrada)
                .debito(movimentoSaida).build();

        SolicitacaoVerificacaoSaldo solicitacaoVerificacoSaldo = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        VerificacaoLimiteService service = new VerificacaoLimiteService();

        ResultadoVerificacaoSaldo resultadoVerificacaoSaldo = service.handle(solicitacaoVerificacoSaldo);

        if (SaldoDentroLimite.class.equals(resultadoVerificacaoSaldo.getClass())) {
            return CompraDivida.from(transferencia);
        }

        return CompraDivida.solicitarAprovacaoMovimento(transferencia);
    }

    public CompraDivida handle(SolicitarAprovacaoCompra cmd) {

        CompraDivida compraDivida = cmd.getCompraDivida();

        if (cmd.isAprovada()) {
            compraDivida.aprovar();
        } else {
            compraDivida.recusar();
        }

        return compraDivida;
    }

}
