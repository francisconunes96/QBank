package com.totvs.tj.qbank.domain;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.totvs.tj.qbank.app.ContaService;
import com.totvs.tj.qbank.app.SolicitacaoVerificacaoSaldo;
import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;
import com.totvs.tj.qbank.domain.documento.CNPJ;
import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.empresa.ResponsavelId;

public class MovimentacaoTest {
    
    @Test
    public void aoSolicitarVerificaoSaldoDeveEstarDentroDoLimiteTest() {
        
        //Given        
        Empresa empresa = Empresa.builder()
                .id(EmpresaId.generate())
                .cnpj(CNPJ.of("11057774000175"))
                .nome("TOTVS")
                .responsavel(ResponsavelId.generate())
                .valorMercado(BigDecimal.valueOf(10000))
                .quantidadeFuncionarios(2)
                .build();
        
        Conta conta = Conta.builder()
                .id(ContaId.generate())
                .empresa(empresa)
                .calcularLimite()
                .build();
         
        conta.creditar(BigDecimal.valueOf(2000));
                
        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(conta, BigDecimal.valueOf(1000));

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);
        
        //When
        boolean estaNoLimite = contaService.handle(cmd);
        
        //Then      
        assertTrue(estaNoLimite);
    }
    
    @Test
    public void aoSolicitarVerificaoSaldoUtilizandoLimiteDeveEstarDentroDoLimiteTest() {
        
        //Given
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
                               
        conta.debitar(BigDecimal.valueOf(1000));
        
        SolicitacaoVerificacaoSaldo cmd = SolicitacaoVerificacaoSaldo.of(conta, BigDecimal.valueOf(1000));

        ContaRepository contaRepository = new ContaRepositoryMock();
        ContaService contaService = new ContaService(contaRepository);
        
        //When
        boolean estaNoLimite = contaService.handle(cmd);
        
        //Then      
        assertTrue(estaNoLimite);               
    }
        
    @Test
    public void aoCreditarDeveAumentarSaldoTest() throws Exception {
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
                
        conta.creditar(BigDecimal.valueOf(100));        
                
        //WHEN        
        BigDecimal valorDebito = BigDecimal.valueOf(50);
        conta.debitar(valorDebito);
        
        //THEN
        assertTrue(conta.getSaldo().compareTo(BigDecimal.valueOf(50)) == 0);         
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
