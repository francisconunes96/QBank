package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.MovimentoService;
import com.totvs.tj.qbank.app.ResultadoVerificacaoSaldo;
import com.totvs.tj.qbank.app.SaldoDentroLimite;
import com.totvs.tj.qbank.app.SaldoExcedido;
import com.totvs.tj.qbank.app.SolicitacaoAprovacaoGerente;
import com.totvs.tj.qbank.app.SolicitacaoVerificacaoSaldo;
import com.totvs.tj.qbank.app.VerificacaoLimiteService;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoRepository;

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

        VerificacaoLimiteService service = new VerificacaoLimiteService();

        //When
        ResultadoVerificacaoSaldo estaNoLimite = service.handle(cmd);

        //Then      
        assertNotNull(estaNoLimite);
        assertTrue(estaNoLimite instanceof SaldoDentroLimite);
    }

    @Test
    public void aoSolicitarVerificaoSaldoUtilizandoLimiteDeveEstarDentroDoLimiteTest() {

        //Given
        conta.debitar(BigDecimal.valueOf(1000));

        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        VerificacaoLimiteService service = new VerificacaoLimiteService();

        //When
        ResultadoVerificacaoSaldo estaNoLimite = service.handle(cmd);

        //Then      
        assertNotNull(estaNoLimite);
        assertTrue(estaNoLimite instanceof SaldoDentroLimite);
    }

    @Test
    public void aoSolicitarVerificacaoSaldoUtilizandoLimiteDeveEstarForaLimiteTest() {
        //Given
        conta.debitar(BigDecimal.valueOf(10000));

        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(movimentoSaida);

        VerificacaoLimiteService service = new VerificacaoLimiteService();

        //When
        ResultadoVerificacaoSaldo estaNoLimite = service.handle(cmd);

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

        VerificacaoLimiteService service = new VerificacaoLimiteService();

        //When
        ResultadoVerificacaoSaldo estaNoLimite = service.handle(cmd);

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

        MovimentoRepository movimentoRepository = new MovimentoRepositoryMock();
        MovimentoService movimentoService = new MovimentoService(movimentoRepository);

        Movimento movimentoAprovado = movimentoService.handle(aprovacaoGerente);

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

        MovimentoRepository movimentoRepository = new MovimentoRepositoryMock();
        MovimentoService movimentoService = new MovimentoService(movimentoRepository);

        Movimento movimentoRecusado = movimentoService.handle(aprovacaoGerente);

        //Then
        assertNotNull(movimentoRecusado);
        assertTrue(movimentoRecusado.isRecusado());
    }
              
    static class MovimentoRepositoryMock implements MovimentoRepository {

        private final Map<MovimentoId, Movimento> movimentos = new LinkedHashMap<>();
        
		@Override
		public void save(Movimento movimento) {
			movimentos.put(movimento.getId(), movimento);			
		}

		@Override
		public Movimento getOne(MovimentoId id) {
			return movimentos.get(id);
		}

    }

}
