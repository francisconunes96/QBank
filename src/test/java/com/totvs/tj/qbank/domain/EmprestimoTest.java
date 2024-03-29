package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.EmprestimoService;
import com.totvs.tj.qbank.app.SolicitacaoAprovacaoEmprestimo;
import com.totvs.tj.qbank.app.SolicitacaoEmprestimo;
import com.totvs.tj.qbank.app.SolicitacaoQuitacaoDivida;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.movimentacao.Emprestimo;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoRepository;
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
    public void aoSolicitarEmprestimoDeveRealizarEmprestimoTest() {

        // Given
        BigDecimal saldoAntigo = conta.getSaldo();

        // When
        SolicitacaoEmprestimo cmd = SolicitacaoEmprestimo.from(BigDecimal.valueOf(1000), conta);

        EmprestimoRepository emprestimoRepository = new EmprestimoRepositoryMock();
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository);
        
        Emprestimo emprestimoEfetuado = emprestimoService.handle(cmd);

        // Then
        assertNotNull(emprestimoEfetuado);
        assertTrue(Emprestimo.Situacao.LIBERADO.equals(emprestimoEfetuado.getSituacao()));
        assertTrue(conta.getSaldo().compareTo(saldoAntigo.add(emprestimoEfetuado.getValor())) == 0);
    }

    @Test
    public void aoSolicitarEmprestimoDeveAguardarAprovacaoGerenteTest() {

        // When
        SolicitacaoEmprestimo cmd = SolicitacaoEmprestimo.from(BigDecimal.valueOf(2500),conta);

        EmprestimoRepository emprestimoRepository = new EmprestimoRepositoryMock();
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository);

        Emprestimo emprestimoAguardandoAprovacao = emprestimoService.handle(cmd);

        // Then
        assertTrue(Emprestimo.Situacao.AGUARDANDO_APROVACAO.equals(emprestimoAguardandoAprovacao.getSituacao()));
    }

    @Test
    public void aoAprovarMovimentoEmprestimoDeveRealizarEmprestimoTest() {

        // Given
        Movimento movimento = Movimento.builder()
                .id(MovimentoId.generate())
                .credito()
                .emprestimo()
                .conta(conta)
                .valor(BigDecimal.valueOf(2500))
                .build();

        Emprestimo emprestimo = Emprestimo.builder()
                .id(EmprestimoId.generate())
                .movimento(movimento)
                .build();

        emprestimo.aguardarAprovacao();

        //When
        EmprestimoRepository emprestimoRepository = new EmprestimoRepositoryMock();
        
        emprestimoRepository.save(emprestimo);
        
        SolicitacaoAprovacaoEmprestimo cmd = SolicitacaoAprovacaoEmprestimo
                .from(emprestimo.getId(), SolicitacaoAprovacaoEmprestimo.Situacao.APROVADA);
        
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository);;

        Emprestimo emprestimoAprovado = emprestimoService.handle(cmd);

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
                .credito()
                .emprestimo()
                .conta(conta)
                .valor(BigDecimal.valueOf(2500))
                .build();

        Emprestimo emprestimo = Emprestimo.builder()
                .id(EmprestimoId.generate())
                .movimento(movimento)
                .build();

        emprestimo.aguardarAprovacao();

        // When
        EmprestimoRepository emprestimoRepository = new EmprestimoRepositoryMock();
        
        emprestimoRepository.save(emprestimo);
        
        SolicitacaoAprovacaoEmprestimo cmd = SolicitacaoAprovacaoEmprestimo
                .from(emprestimo.getId(), SolicitacaoAprovacaoEmprestimo.Situacao.RECUSADA);
        
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository);

        Emprestimo emprestimoRecusado = emprestimoService.handle(cmd);

        // Then
        assertNotNull(emprestimoRecusado);
        assertTrue(Emprestimo.Situacao.RECUSADO.equals(emprestimoRecusado.getSituacao()));
        assertTrue(Movimento.Situacao.RECUSADO.equals(emprestimoRecusado.getMovimento().getSituacao()));
    }

    @Test
    public void aoQuitarEmprestimoTest() {

        //Given
        Movimento movimento = Movimento.builder()
                .id(MovimentoId.generate())
                .debito()
                .emprestimo()
                .conta(conta)
                .valor(BigDecimal.valueOf(2500))
                .build();

        Emprestimo emprestimo = Emprestimo.builder()
                .id(EmprestimoId.generate())
                .movimento(movimento)
                .build();

        boolean emprestimoQuitado = emprestimo.quitar();

        //Then
        assertTrue(emprestimoQuitado);
        assertTrue(Emprestimo.Situacao.QUITADO.compareTo(emprestimo.getSituacao()) == 0);

    }

    @Test
    public void aoSolicitarQuitacaoDeEmprestimoTest() throws Exception {

        //Given
        Movimento movimento = Movimento.builder()
                .id(MovimentoId.generate())
                .debito()
                .emprestimo()
                .conta(conta)
                .valor(BigDecimal.valueOf(1250))
                .build();

        Emprestimo emprestimo = Emprestimo.builder()
                .id(EmprestimoId.generate())
                .movimento(movimento)
                .build();

        //When
        EmprestimoRepository emprestimoRepository = new EmprestimoRepositoryMock();
        
        emprestimoRepository.save(emprestimo);
        
        SolicitacaoQuitacaoDivida cmd = SolicitacaoQuitacaoDivida
                .from(emprestimo.getId());
        
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository);
        Emprestimo emprestimoQuitado = emprestimoService.handle(cmd);

        //Then
        assertTrue(Emprestimo.Situacao.QUITADO.equals(emprestimoQuitado.getSituacao()));

    }

    @Test(expected = Exception.class)
    public void aoRecusarSolicitacaoDeQuitacaoEmprestimo() throws Exception {

        //Given
        Movimento movimento = Movimento.builder()
                .id(MovimentoId.generate())
                .debito()
                .emprestimo()
                .conta(conta)
                .valor(BigDecimal.valueOf(2000))
                .build();

        Emprestimo emprestimo = Emprestimo.builder()
                .id(EmprestimoId.generate())
                .movimento(movimento)
                .build();

        //When
        EmprestimoRepository emprestimoRepository = new EmprestimoRepositoryMock();
        
        emprestimoRepository.save(emprestimo);
        
        SolicitacaoQuitacaoDivida cmd = SolicitacaoQuitacaoDivida
                .from(emprestimo.getId());
        
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository);

        emprestimoService.handle(cmd);

    }

    static class EmprestimoRepositoryMock implements EmprestimoRepository {

        private final Map<EmprestimoId, Emprestimo> emprestimos = new LinkedHashMap<>();

        @Override
        public void save(Emprestimo emprestimo) {
            emprestimos.put(emprestimo.getId(), emprestimo);
        }

        @Override
        public Emprestimo getOne(EmprestimoId id) {
            return emprestimos.get(id);
        }
    }
}
