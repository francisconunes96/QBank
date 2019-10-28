package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;

public class ContaService {
    
    private ContaRepository repository;
    
    public ContaService(ContaRepository repository) {
        this.repository = repository;
    }
    
    public ContaId handle(SolicitacaoAberturaConta cmd) {
        
        ContaId idConta = ContaId.generate();
        
        Conta conta = Conta.builder()
                .id(idConta)
                .empresa(cmd.getEmpresa())
                .calcularLimite()
            .build();
        
        repository.save(conta);
        
        return idConta; 
    }
    
    public void handle(SolicitacaoAumentoLimiteEmergencial cmd) {
        Conta conta = repository.getOne(cmd.getIdConta());
        
        conta.aumentarLimite();
        
        repository.save(conta);
    }

    public void handle(SuspenderConta cmd) {
        
        Conta conta = repository.getOne(cmd.getConta());
        
        conta.suspender();     
        
        repository.save(conta);
    }
    
}
