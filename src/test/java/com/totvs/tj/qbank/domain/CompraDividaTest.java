package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.ContaService;
import com.totvs.tj.qbank.app.SolicitacaoCompraDivida;
import com.totvs.tj.qbank.app.SolicitarAprovacaoCompra;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida;
import com.totvs.tj.qbank.domain.movimentacao.CompraDivida.Situacao;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaId;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.Transferencia;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;

public class CompraDividaTest {

    @Test
    public void aoSolicitarCompraDividaDeOutraEmpresaTest() {

	// GIVEN
	Empresa empresaSolicitante = Empresa.builder()
		.id(EmpresaId.generate())
		.cnpj("11057774000175")
		.responsavel("23061790004")
		.valorMercado(BigDecimal.valueOf(10000))
		.quantidadeFuncionarios(2)
		.build();

	Conta contaSolicitante = Conta.builder()
		.id(ContaId.generate())
		.empresa(empresaSolicitante)
		.calcularLimite()
		.build();

	Empresa empresaSolicitada = Empresa.builder()
		.id(EmpresaId.generate())
		.cnpj("40121037000192")
		.responsavel("30877250057")
		.valorMercado(BigDecimal.valueOf(15000))
		.quantidadeFuncionarios(2)
		.build();

	Conta contaSolicitada = Conta.builder()
		.id(ContaId.generate())
		.empresa(empresaSolicitada)
		.calcularLimite()
		.build();

	contaSolicitada.debitar(BigDecimal.valueOf(3750));

	ContaRepository contaRepository = new ContaRepositoryMock();
	ContaService contaService = new ContaService(contaRepository);

	// WHEN
	SolicitacaoCompraDivida cmd = SolicitacaoCompraDivida.from(contaSolicitante, contaSolicitada);

	CompraDivida compraDivida = contaService.handle(cmd);

	// THEN
	assertNotNull(compraDivida);
	assertTrue(CompraDivida.Situacao.INICIADA.equals(compraDivida.getSituacao()));
    }

    @Test
    public void aoComprarDivida() {

	// GIVEN
	Empresa empresaSolicitante = Empresa.builder()
		.id(EmpresaId.generate())
		.cnpj("11057774000175")
		.responsavel("23061790004")
		.valorMercado(BigDecimal.valueOf(10000))
		.quantidadeFuncionarios(2)
		.build();

	Conta contaSolicitante = Conta.builder()
		.id(ContaId.generate())
		.empresa(empresaSolicitante)
		.calcularLimite()
		.build();

	Empresa empresaSolicitada = Empresa.builder()
		.id(EmpresaId.generate())
		.cnpj("40121037000192")
		.responsavel("30877250057")
		.valorMercado(BigDecimal.valueOf(15000))
		.quantidadeFuncionarios(2)
		.build();

	Conta contaSolicitada = Conta.builder()
		.id(ContaId.generate())
		.empresa(empresaSolicitada)
		.calcularLimite()
		.build();

	contaSolicitada.debitar(BigDecimal.valueOf(3750));

	BigDecimal valorMovimentoSolicitada = contaSolicitada.getSaldo();
	BigDecimal valorMovimentoSolicitante = contaSolicitada.getSaldo();

	Movimento movimentoSolicitante = Movimento.builder()
		.id(MovimentoId.generate())
		.tipoSaida()
		.conta(contaSolicitante)
		.valor(valorMovimentoSolicitante.negate())
		.build();

	Movimento movimentoSolicitada = Movimento.builder()
		.id(MovimentoId.generate())
		.tipoEntrada()
		.conta(contaSolicitada)
		.valor(valorMovimentoSolicitada.negate())
		.build();

	Transferencia transferencia = Transferencia.builder()
		.id(TransferenciaId.generate())
		.credito(movimentoSolicitada)
		.debito(movimentoSolicitante)
		.build();

	CompraDivida compraDivida = CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.INICIADA);

	// WHEN
	boolean compraEfetuada = compraDivida.efetuar();

	// THEN
	assertTrue(compraEfetuada);
	assertTrue(contaSolicitada.getSaldo().compareTo(BigDecimal.ZERO) == 0);
	assertTrue(contaSolicitante.getSaldo().compareTo(valorMovimentoSolicitante) == 0);
    }

    @Test
    public void aoSolicitarACompraDividaAEmpresaEndividadaDeveAprovarACompra() {
	// GIVEN
	Empresa empresaSolicitante = Empresa.builder()
		.id(EmpresaId.generate())
		.cnpj("11057774000175")
		.responsavel("23061790004")
		.valorMercado(BigDecimal.valueOf(10000))
		.quantidadeFuncionarios(2)
		.build();

	Conta contaSolicitante = Conta.builder()
		.id(ContaId.generate())
		.empresa(empresaSolicitante)
		.calcularLimite()
		.build();

	Empresa empresaSolicitada = Empresa.builder()
		.id(EmpresaId.generate())
		.cnpj("40121037000192")
		.responsavel("30877250057")
		.valorMercado(BigDecimal.valueOf(15000))
		.quantidadeFuncionarios(2)
		.build();

	Conta contaSolicitada = Conta.builder()
		.id(ContaId.generate())
		.empresa(empresaSolicitada)
		.calcularLimite()
		.build();

	contaSolicitada.debitar(BigDecimal.valueOf(3750));

	BigDecimal valorMovimentoSolicitada = contaSolicitada.getSaldo();
	BigDecimal valorMovimentoSolicitante = contaSolicitada.getSaldo();

	Movimento movimentoSolicitante = Movimento.builder()
		.id(MovimentoId.generate())
		.tipoSaida()
		.conta(contaSolicitante)
		.valor(valorMovimentoSolicitante.negate())
		.build();

	Movimento movimentoSolicitada = Movimento.builder()
		.id(MovimentoId.generate())
		.tipoEntrada()
		.conta(contaSolicitada)
		.valor(valorMovimentoSolicitada.negate())
		.build();

	Transferencia transferencia = Transferencia.builder()
		.id(TransferenciaId.generate())
		.credito(movimentoSolicitada)
		.debito(movimentoSolicitante)
		.build();

	CompraDivida compraDivida = CompraDivida.from(CompraDividaId.generate(), transferencia, Situacao.INICIADA);

	ContaRepository contaRepository = new ContaRepositoryMock();
	ContaService contaService = new ContaService(contaRepository);

	// WHEN
	SolicitarAprovacaoCompra cmd = SolicitarAprovacaoCompra.from(compraDivida,
		SolicitarAprovacaoCompra.Situacao.APROVADA);

	CompraDivida compraDividaAprovada = contaService.handle(cmd);

	// THEN
	assertTrue(CompraDivida.Situacao.APROVADA.equals(compraDividaAprovada.getSituacao()));
    }

    static class ContaRepositoryMock implements ContaRepository {

	private final Map<ContaId, Conta> contas = new LinkedHashMap<>();

	@Override
	public void save(Conta conta) {
	    contas.put(conta.getId(), conta);
	}

	@Override
	public Conta getOne(ContaId id) {
	    return contas.get(id);
	}

    }

}
