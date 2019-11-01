package com.totvs.tj.qbank.app;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaId;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.Transferencia;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida.Situacao;

public class CompraDividaService {
	
	public Transferencia handle(SolicitacaoTransferencia cmd) {

		Transferencia transferencia = cmd.getTransferencia();

		if (transferencia.transferir()) {
			transferencia.finalizar();
		}

		return transferencia;
	}

	public CompraDivida handle(SolicitacaoCompraDivida cmd) {

		Conta solicitada = cmd.getContaSolicitada();
		Conta solicitante = cmd.getContaSolicitante();

		BigDecimal valorMovimento = solicitada.getSaldo();

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

		return CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.RECUSADA);
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
