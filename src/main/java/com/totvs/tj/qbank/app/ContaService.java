package com.totvs.tj.qbank.app;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaId;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.SolicitarTransferencia;
import com.totvs.tj.qbank.domain.movimentacao.Transferencia;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida.Situacao;

public class ContaService {

    private ContaRepository repository;

    public ContaService(ContaRepository repository) {
	this.repository = repository;
    }

    public ContaId handle(SolicitacaoAberturaConta cmd) {

	ContaId idConta = ContaId.generate();

	Conta conta = Conta.builder()
		.id(idConta)
		.empresa(cmd.getEmpresa())
		.calcularLimite()
		.build();

	repository.save(conta);

	return idConta;
    }

    public void handle(SolicitacaoAumentoLimiteEmergencial cmd) throws Exception {
	Conta conta = repository.getOne(cmd.getIdConta());

	if (!conta.aumentarLimite()) {
	    throw new Exception("Só é permitido a solicitação de crédito emergencial uma vez");
	}

	repository.save(conta);
    }

    public void handle(SuspenderConta cmd) {

	Conta conta = repository.getOne(cmd.getConta());

	conta.suspender();

	repository.save(conta);
    }

    public ResultadoVerificacaoSaldo handle(SolicitacaoVerificacaoSaldo cmd) {
	Movimento movimento = cmd.getMovimento();
	Conta conta = movimento.getConta();

	if (conta.estaDentroDoLimite(movimento.getValor())) {
	    return SaldoDentroLimite.from(movimento);
	}

	return SaldoExcedido.from(movimento);
    }

    public Movimento handle(SolicitacaoAprovacaoGerente cmd) {

	Movimento movimento = cmd.getMovimento();

	if (cmd.isAprovada()) {
	    movimento.aprovar();
	} else {
	    movimento.recusar();
	}

	return movimento;
    }

    public Transferencia handle(SolicitarTransferencia cmd) {

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

	Movimento movimentoSaida = Movimento.builder()
		.id(MovimentoId.generate())
		.tipoSaida()
		.conta(solicitante)
		.valor(valorMovimento)
		.build();

	Movimento movimentoEntrada = Movimento.builder()
		.id(MovimentoId.generate())
		.tipoEntrada()
		.conta(solicitada)
		.valor(valorMovimento)
		.build();

	Transferencia transferencia = Transferencia.builder()
		.id(TransferenciaId.generate())
		.credito(movimentoEntrada)
		.debito(movimentoSaida)
		.build();

	SolicitacaoVerificacaoSaldo solicitacaoVerificacoSaldo = SolicitacaoVerificacaoSaldo.of(movimentoSaida);
	ResultadoVerificacaoSaldo resultadoVerificacaoSaldo = this.handle(solicitacaoVerificacoSaldo);
	
	if (SaldoDentroLimite.class.equals(resultadoVerificacaoSaldo.getClass())) {
	    return CompraDivida.from(transferencia);
	}

	return CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.RECUSADA);

    }
}
