package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.ContaService;
import com.totvs.tj.qbank.app.ResultadoVerificacaoSaldo;
import com.totvs.tj.qbank.app.SaldoDentroLimite;
import com.totvs.tj.qbank.app.SaldoExcedido;
import com.totvs.tj.qbank.app.SolicitacaoAprovacaoEmprestimo;
import com.totvs.tj.qbank.app.SolicitacaoAprovacaoGerente;
import com.totvs.tj.qbank.app.SolicitacaoVerificacaoSaldo;
import com.totvs.tj.qbank.app.SolicitacaoEmprestimo;
import com.totvs.tj.qbank.app.SolicitacaoTransferencia;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.movimentacao.Emprestimo;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.Transferencia;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;

public class MovimentacaoTest {

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

    Movimento movimentoSaida = Movimento.builder()
            .id(MovimentoId.generate())
            .tipoSaida()
            .conta(conta)
            .valor(BigDecimal.valueOf(1000))
            .build();

    @Test
    public void aoSolicitarVerificaoSaldoDeveEstarDentroDoLimiteTest() {

        //Given        
        conta.creditar(BigDecimal.valueOf(2000));

        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);

        //When
        ResultadoVerificacaoSaldo estaNoLimite = contaService.handle(cmd);

        //Then      
        assertNotNull(estaNoLimite);
        assertTrue(estaNoLimite instanceof SaldoDentroLimite);
    }

    @Test
    public void aoSolicitarVerificaoSaldoUtilizandoLimiteDeveEstarDentroDoLimiteTest() {

        //Given
        conta.debitar(BigDecimal.valueOf(1000));

        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);

        //When
        ResultadoVerificacaoSaldo estaNoLimite = contaService.handle(cmd);

        //Then      
        assertNotNull(estaNoLimite);
        assertTrue(estaNoLimite instanceof SaldoDentroLimite);
    }

    @Test
    public void aoSolicitarVerificacaoSaldoUtilizandoLimiteDeveEstarForaLimiteTest() {
        //Given
        conta.debitar(BigDecimal.valueOf(10000));

        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);

        //When
        ResultadoVerificacaoSaldo estaNoLimite = contaService.handle(cmd);

        //Then
        assertNotNull(estaNoLimite);
        assertTrue(estaNoLimite instanceof SaldoExcedido);
    }

    @Test
    public void aoSolicitarVerificacaoSaldoDeveEstarForaLimiteTest() {
        //Given
        Movimento movimentoSaida = Movimento.builder()
                .id(MovimentoId.generate())
                .tipoSaida()
                .conta(conta)
                .valor(BigDecimal.valueOf(15000))
                .build();

        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);

        //When
        ResultadoVerificacaoSaldo estaNoLimite = contaService.handle(cmd);

        //Then
        assertNotNull(estaNoLimite);
        assertTrue(estaNoLimite instanceof SaldoExcedido);
    }

    @Test
    public void aoCreditarDeveAumentarSaldoTest() throws Exception {
        // WHEN
        BigDecimal valorOriginal = conta.getSaldo();
        BigDecimal valorCredito = BigDecimal.valueOf(100);
        conta.creditar(valorCredito);

        //THEN
        assertTrue(conta.getSaldo().compareTo(valorOriginal) == 1);
    }

    @Test
    public void aoDebitarDeveDiminuirSaldoTest() {

        //GIVEN
        conta.creditar(BigDecimal.valueOf(100));

        //WHEN        
        BigDecimal valorDebito = BigDecimal.valueOf(50);
        conta.debitar(valorDebito);

        //THEN
        assertTrue(conta.getSaldo().compareTo(BigDecimal.valueOf(50)) == 0);
    }

    @Test
    public void aoExcederSaldoGerenteAprovaMovimentacaoTest() {

        //Given
        ResultadoVerificacaoSaldo resultadoEvt = SaldoExcedido.from(movimentoSaida);

        //When
        SolicitacaoAprovacaoGerente aprovacaoGerente = SolicitacaoAprovacaoGerente
                .from(resultadoEvt, SolicitacaoAprovacaoGerente.Situacao.APROVADA);

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);

        Movimento movimentoAprovado = contaService.handle(aprovacaoGerente);

        //Then
        assertNotNull(movimentoAprovado);
        assertTrue(movimentoAprovado.isAprovado());
    }

    @Test
    public void aoExcederSaldoGerenteReprovaMovimentacaoTest() {

        //Given
        ResultadoVerificacaoSaldo resultadoEvt = SaldoExcedido.from(movimentoSaida);

        //When
        SolicitacaoAprovacaoGerente aprovacaoGerente = SolicitacaoAprovacaoGerente
                .from(resultadoEvt, SolicitacaoAprovacaoGerente.Situacao.RECUSADA);

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);

        Movimento movimentoRecusado = contaService.handle(aprovacaoGerente);

        //Then
        assertNotNull(movimentoRecusado);
        assertTrue(movimentoRecusado.isRecusado());
    }

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
        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);
        
        Transferencia transferenciaEfetuada = contaService.handle(cmd);
        
        //Then
        assertTrue(Transferencia.Situacao.FINALIZADA.equals(transferenciaEfetuada.getSituacao()));    	
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
