package com.totvs.tj.qbank.domain.movimentacao;

public interface EmprestimoRepository {
    
    void save(Emprestimo emprestimo);
    
    Emprestimo getOne(EmprestimoId id);

}
