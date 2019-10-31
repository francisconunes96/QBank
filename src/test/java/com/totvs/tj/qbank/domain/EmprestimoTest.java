package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.totvs.tj.qbank.app.ContaService;
import com.totvs.tj.qbank.app.SolicitacaoAprovacaoEmprestimo;
import com.totvs.tj.qbank.app.SolicitarEmprestimo;
import com.totvs.tj.qbank.domain.MovimentacaoTest.ContaRepositoryMock;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.movimentacao.Emprestimo;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;

public class EmprestimoTest {
	
	Empresa empresa = Empresa.builder()
            .id(EmpresaId.generate())
            .cnpj("11057774000175")
            .responsavel("23061790004")
            .valorMercado(BigDecimal.valueOf(10000))
            .quantidadeFuncionarios(2)
            .build();

    ContaId idConta = ContaId.generate();

    Conta conta = Conta.builder()
            .id(ContaId.generate())
            .empresa(empresa)
            .calcularLimite()
            .build();
	
	@Test
    public void aoRealizarEmprestimoTest() {
    	
    	// Given    
    	Movimento movimento = Movimento.builder()
    			.id(MovimentoId.generate())
    			.tipoEntrada()
    			.conta(conta)
    			.valor(BigDecimal.valueOf(1000))
    			.build();
    	    	
    	Emprestimo emprestimo = Emprestimo.builder()
    			.id(EmprestimoId.generate())
    			.movimento(movimento)    			    		
    			.build();
    	
    	// When
    	boolean emprestimoEfetuado = emprestimo.emprestar();
    	
    	// Then
    	assertTrue(emprestimoEfetuado);    	
    }
    
    @Test
    public void aoSolicitarEmprestimoDeveRealizarEmprestimoTest() {
    	
    	// Given
    	Movimento movimento = Movimento.builder()
    			.id(MovimentoId.generate())
    			.tipoEntrada()
    			.conta(conta)
    			.valor(BigDecimal.valueOf(1000))
    			.build();
    	
    	Emprestimo emprestimo = Emprestimo.builder()
    			.id(EmprestimoId.generate())
    			.movimento(movimento)
    			.build();
    	
    	BigDecimal saldoAntigo = conta.getSaldo();
    	
    	// When
    	SolicitarEmprestimo cmd = SolicitarEmprestimo.from(emprestimo);
    	
    	ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);
        
        Emprestimo emprestimoEfetuado = contaService.handle(cmd);
    	
    	// Then
    	assertNotNull(emprestimoEfetuado);
    	assertTrue(Emprestimo.Situacao.LIBERADO.equals(emprestimoEfetuado.getSituacao()));
    	assertTrue(conta.getSaldo().compareTo(saldoAntigo.add(emprestimoEfetuado.getValor())) == 0);
    }
    
    @Test
    public void aoSolicitarEmprestimoDeveAguardarAprovacaoGerenteTest() {
    	
    	// Given
    	Movimento movimento = Movimento.builder()
    			.id(MovimentoId.generate())
    			.tipoEntrada()
    			.conta(conta)
    			.valor(BigDecimal.valueOf(2500))
    			.build();
    	
    	Emprestimo emprestimo = Emprestimo.builder()
    			.id(EmprestimoId.generate())
    			.movimento(movimento)   			
    			.build();
    	
    	// When
    	SolicitarEmprestimo cmd = SolicitarEmprestimo.from(emprestimo);
    	
    	ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);
        
        Emprestimo emprestimoAguardandoAprovacao = contaService.handle(cmd);
        
        // Then
        assertTrue(Emprestimo.Situacao.AGUARDANDO_APROVACAO.equals(emprestimoAguardandoAprovacao.getSituacao()));    	    
    }
    
    @Test
    public void aoAprovarMovimentoEmprestimoDeveRealizarEmprestimoTest() {
    	
    	// Given
    	Movimento movimento = Movimento.builder()
    			.id(MovimentoId.generate())
    			.tipoEntrada()
    			.conta(conta)
    			.valor(BigDecimal.valueOf(2500))
    			.build();
    	
    	Emprestimo emprestimo = Emprestimo.builder()
    			.id(EmprestimoId.generate())
    			.movimento(movimento)   			
    			.build();
    	
    	emprestimo.aguardarAprovacao();
    	        
        //When
        SolicitacaoAprovacaoEmprestimo cmd = SolicitacaoAprovacaoEmprestimo
                .from(emprestimo, SolicitacaoAprovacaoEmprestimo.Situacao.APROVADA);
        
        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);
        
        Emprestimo emprestimoAprovado = contaService.handle(cmd);
    	
    	// Then
        assertNotNull(emprestimoAprovado);
        assertTrue(Emprestimo.Situacao.LIBERADO.equals(emprestimoAprovado.getSituacao()));
        assertTrue(Movimento.Situacao.APROVADO.equals(emprestimoAprovado.getMovimento().getSituacao()));
    }  
    
    @Test
    public void aoRecusarMovimentoEmprestimoTest() {
    	
    	// Given
    	Movimento movimento = Movimento.builder()
    			.id(MovimentoId.generate())
    			.tipoEntrada()
    			.conta(conta)
    			.valor(BigDecimal.valueOf(2500))
    			.build();
    	
    	Emprestimo emprestimo = Emprestimo.builder()
    			.id(EmprestimoId.generate())
    			.movimento(movimento)   			
    			.build();
    	
    	emprestimo.aguardarAprovacao();
    	
    	// When
        SolicitacaoAprovacaoEmprestimo cmd = SolicitacaoAprovacaoEmprestimo
                .from(emprestimo, SolicitacaoAprovacaoEmprestimo.Situacao.RECUSADA);
        
        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);
        
        Emprestimo emprestimoRecusado = contaService.handle(cmd);
        
        // Then
        assertNotNull(emprestimoRecusado);
        assertTrue(Emprestimo.Situacao.RECUSADO.equals(emprestimoRecusado.getSituacao()));
        assertTrue(Movimento.Situacao.RECUSADO.equals(emprestimoRecusado.getMovimento().getSituacao()));    	
    }
	
}
