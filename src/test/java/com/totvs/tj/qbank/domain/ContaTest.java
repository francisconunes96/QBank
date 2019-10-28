package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.ContaService;
import com.totvs.tj.qbank.app.SolicitacaoAberturaConta;
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
        assertEquals(conta.getLimite(), empresa.getValorMercado().divide(BigDecimal.valueOf(empresa.getQuantidadeFuncionarios())));
        
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
    
    static class ContaRepositoryMock implements ContaRepository {

        private final Map<ContaId, Conta> contas = new LinkedHashMap<>();

        @Override
        public void save(Conta conta) {
            contas.put(conta.getId(), conta);
        }
        
    }
    
    
}
