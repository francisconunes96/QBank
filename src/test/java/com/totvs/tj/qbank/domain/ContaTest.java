package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.ContaService;
import com.totvs.tj.qbank.app.SolicitacaoAberturaConta;
import com.totvs.tj.qbank.app.SolicitacaoAumentoLimiteEmergencial;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;
import com.totvs.tj.qbank.domain.documento.CNPJ;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.empresa.ResponsavelId;

public class ContaTest {

    @Test
    public void aoCriarUmaContaTest() {
        //Given        
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj(CNPJ.of("11057774000175"))
                .nome("TOTVS")
                .responsavel(ResponsavelId.generate())
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
    public void aoSolicitarAberturaContaDeveAbrirUmaContaTest() {

        //Given
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj(CNPJ.of("11057774000175"))
                .nome("TOTVS")
                .responsavel(ResponsavelId.generate())
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
    public void aoSolicitarAumentoLimiteEmergencialTest() {

        //GIVEN

        ContaId idConta = ContaId.generate();

        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj(CNPJ.of("11057774000175"))
                .nome("TOTVS")
                .responsavel(ResponsavelId.generate())
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
