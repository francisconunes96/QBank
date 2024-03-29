package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.ContaService;
import com.totvs.tj.qbank.app.SolicitacaoAberturaConta;
import com.totvs.tj.qbank.app.SolicitacaoAumentoLimiteEmergencial;
import com.totvs.tj.qbank.app.SuspenderConta;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;

public class ContaTest {

    @Test
    public void aoCriarUmaContaTest() {
        //Given        
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();

        //When
        Conta conta = Conta.builder()
                .id(ContaId.generate())
                .empresa(empresa)
                .calcularLimite()
                .build();

        //Then
        assertNotNull(conta);
        assertEquals(empresa.getId(), conta.getEmpresa());
        assertEquals(conta.getLimite(),
                empresa.getValorMercado().divide(BigDecimal.valueOf(empresa.getQuantidadeFuncionarios())));

    }

    @Test
    public void aoAbrirContaComLimiteMaiorQueOPermitidoTest() {
        //Given        
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(100000))
                .quantidadeFuncionarios(2)
                .build();

        //When
        Conta conta = Conta.builder()
                .id(ContaId.generate())
                .empresa(empresa)
                .calcularLimite()
                .build();

        //Then
        assertTrue(conta.getLimite().equals(BigDecimal.valueOf(15000)));
    }

    @Test
    public void aoSolicitarAberturaContaDeveAbrirUmaContaTest() {

        //Given
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();

        SolicitacaoAberturaConta cmd = SolicitacaoAberturaConta.builder()
                .empresa(empresa)
                .build();

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);

        //When
        ContaId id = contaService.handle(cmd);

        //Then
        assertNotNull(id);
    }

    @Test
    public void aoSolicitarAumentoLimiteEmergencialTest() throws Exception {

        //GIVEN
        ContaId idConta = ContaId.generate();

        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();

        Conta conta = Conta.builder()
                .id(idConta)
                .empresa(empresa)
                .calcularLimite()
                .build();

        BigDecimal limiteAntigo = conta.getLimite();

        SolicitacaoAumentoLimiteEmergencial cmd = SolicitacaoAumentoLimiteEmergencial.from(idConta);

        ContaRepository repository = new ContaRepositoryMock();
        ContaService service = new ContaService(repository);

        repository.save(conta);

        // WHEN
        service.handle(cmd);

        // THEN
        assertTrue(repository.getOne(idConta).getLimite().equals(limiteAntigo.add(limiteAntigo.divide(BigDecimal.valueOf(2)))));
    }

    @Test(expected = Exception.class)
    public void aoSolicitarAumentoLimiteEmergencialMaisDeUmaVezDeveSerNegadoTest() throws Exception {

        //GIVEN
        ContaId idConta = ContaId.generate();

        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();

        Conta conta = Conta.builder()
                .id(idConta)
                .empresa(empresa)
                .calcularLimite()
                .build();

        SolicitacaoAumentoLimiteEmergencial cmd = SolicitacaoAumentoLimiteEmergencial.from(idConta);

        ContaRepository repository = new ContaRepositoryMock();
        ContaService service = new ContaService(repository);

        repository.save(conta);
        service.handle(cmd);

        // WHEN
        SolicitacaoAumentoLimiteEmergencial cmdSegundaVez = SolicitacaoAumentoLimiteEmergencial.from(idConta);
        service.handle(cmdSegundaVez);
    }

    @Test
    public void aoSuspenderContaExistenteTest() {

        //GIVEN        
        ContaId idConta = ContaId.generate();

        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj("11057774000175")
                .responsavel("23061790004")
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();

        Conta conta = Conta.builder()
                .id(idConta)
                .empresa(empresa)
                .calcularLimite()
                .build();

        SuspenderConta cmd = SuspenderConta.from(idConta);

        ContaRepository repository = new ContaRepositoryMock();
        ContaService service = new ContaService(repository);

        repository.save(conta);

        //WHEN
        service.handle(cmd);

        //THEN
        assertFalse(repository.getOne(idConta).isDisponivel());
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
