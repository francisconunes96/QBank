package com.totvs.tj.qbank.app;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaRepository;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.Transferencia;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;

public class CompraDividaService {
    
    private CompraDividaRepository repository;

    public CompraDividaService (CompraDividaRepository repository) {
        this.repository = repository;
    }

    public Transferencia handle(SolicitacaoTransferencia cmd) {

        Transferencia transferencia = cmd.getTransferencia();

       transferencia.transferir();
       
        return transferencia;
    }

    public CompraDivida handle(SolicitacaoCompraDivida cmd) {
        
        // TODO: Como fazer por id?
        Conta solicitada = cmd.getContaSolicitada();
        Conta solicitante = cmd.getContaSolicitante();

        BigDecimal valorMovimento = solicitada.getSaldo().negate();

        Movimento movimentoSaida = Movimento.builder().id(MovimentoId.generate()).debito().conta(solicitante).compraDivida()
                .valor(valorMovimento).build();

        Movimento movimentoEntrada = Movimento.builder().id(MovimentoId.generate()).credito().conta(solicitada).compraDivida()
                .valor(valorMovimento).build();

        Transferencia transferencia = Transferencia.builder().id(TransferenciaId.generate()).credito(movimentoEntrada)
                .debito(movimentoSaida).build();

        SolicitacaoVerificacaoSaldo solicitacaoVerificacoSaldo = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        VerificacaoLimiteService service = new VerificacaoLimiteService();

        ResultadoVerificacaoSaldo resultadoVerificacaoSaldo = service.handle(solicitacaoVerificacoSaldo);
        
        CompraDivida compraDivida;

        if (SaldoDentroLimite.class.equals(resultadoVerificacaoSaldo.getClass())) {
            compraDivida = CompraDivida.from(transferencia);
        } else {
            compraDivida = CompraDivida.solicitarAprovacaoMovimento(transferencia);
        }
        
        repository.save(compraDivida);                

        return compraDivida;
    }

    public CompraDivida handle(SolicitarAprovacaoCompra cmd) {

        CompraDivida compraDivida = repository.getOne(cmd.getCompraDividaId());

        if (cmd.isAprovada()) {
            compraDivida.aprovar();
        } else {
            compraDivida.recusar();
        }
        
        repository.save(compraDivida);

        return compraDivida;
    }

}
