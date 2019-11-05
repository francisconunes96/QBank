package com.totvs.tj.qbank.domain.movimentacao;

public interface CompraDividaRepository {
    
    void save(CompraDivida compraDivida);
    
    CompraDivida getOne(CompraDividaId id);

}
