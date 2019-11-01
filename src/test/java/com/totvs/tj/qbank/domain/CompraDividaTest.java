package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.totvs.tj.qbank.app.CompraDividaService;
import com.totvs.tj.qbank.app.SolicitacaoCompraDivida;
import com.totvs.tj.qbank.app.SolicitacaoTransferencia;
import com.totvs.tj.qbank.app.SolicitarAprovacaoCompra;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
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
    public void aoTransferirDeContaUmaParaOutraTest() {
        //Given
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
        
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();
        
        Conta conta = Conta.builder()
                .id(ContaId.generate())
                .empresa(empresa)
                .calcularLimite()
                .build();
        
        Movimento movimentoSaida = Movimento.builder()
                .id(MovimentoId.generate())
                .tipoSaida()
                .conta(conta)
                .valor(BigDecimal.valueOf(1000))
                .build();
    	
        Movimento movimentoEntrada = Movimento.builder()
                .id(MovimentoId.generate())
                .tipoEntrada()
                .conta(contaSolicitada)
                .valor(movimentoSaida.getValor())
                .build();

        Transferencia transferencia = Transferencia.builder()
                .id(TransferenciaId.generate())
                .credito(movimentoEntrada)
                .debito(movimentoSaida)
                .build();
        
        BigDecimal saldoSolicitanteAntigo = conta.getSaldo();
        BigDecimal saldoSolicitadaAntigo = contaSolicitada.getSaldo();        
        
        //When
        boolean transferido = transferencia.transferir();
                
        //Then
        assertTrue(transferido);        
        assertTrue(contaSolicitada.getSaldo().compareTo(saldoSolicitadaAntigo.add(transferencia.getValorCredito())) == 0);
        assertTrue(conta.getSaldo().compareTo(saldoSolicitanteAntigo.subtract(transferencia.getValorDebito())) == 0);        
    }
    
    @Test
    public void aoSolicitarTransferenciaTest() {
    	
    	//Given
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
        
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();
        
        Conta conta = Conta.builder()
                .id(ContaId.generate())
                .empresa(empresa)
                .calcularLimite()
                .build();
        
        Movimento movimentoSaida = Movimento.builder()
                .id(MovimentoId.generate())
                .tipoSaida()
                .conta(conta)
                .valor(BigDecimal.valueOf(1000))
                .build();
    	
        Movimento movimentoEntrada = Movimento.builder()
                .id(MovimentoId.generate())
                .tipoEntrada()
                .conta(contaSolicitada)
                .valor(movimentoSaida.getValor())
                .build();

        Transferencia transferencia = Transferencia.builder()
                .id(TransferenciaId.generate())
                .credito(movimentoEntrada)
                .debito(movimentoSaida)
                .build();
        
        SolicitacaoTransferencia cmd = SolicitacaoTransferencia
        		.from(transferencia);
        
        //When
        CompraDividaService compraDividaService = new CompraDividaService();
        
        Transferencia transferenciaEfetuada = compraDividaService.handle(cmd);
        
        //Then
        assertTrue(Transferencia.Situacao.FINALIZADA.equals(transferenciaEfetuada.getSituacao()));    	
    }	

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
	
		CompraDividaService compraDividaService = new CompraDividaService();
	
		// WHEN
		SolicitacaoCompraDivida cmd = SolicitacaoCompraDivida.from(contaSolicitante, contaSolicitada);
	
		CompraDivida compraDivida = compraDividaService.handle(cmd);
	
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
	
		CompraDividaService compraDividaService = new CompraDividaService();
	
		// WHEN
		SolicitarAprovacaoCompra cmd = SolicitarAprovacaoCompra.from(compraDivida,
			SolicitarAprovacaoCompra.Situacao.APROVADA);
	
		CompraDivida compraDividaAprovada = compraDividaService.handle(cmd);
	
		// THEN
		assertTrue(CompraDivida.Situacao.APROVADA.equals(compraDividaAprovada.getSituacao()));
    }

}
