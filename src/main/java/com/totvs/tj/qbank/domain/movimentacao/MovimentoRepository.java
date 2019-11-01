package com.totvs.tj.qbank.domain.movimentacao;

public interface MovimentoRepository {
	
	void save(Movimento movimento);
    
    Movimento getOne(MovimentoId id);

}
