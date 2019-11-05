package com.totvs.tj.qbank.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totvs.tj.qbank.domain.movimentacao.CompraDivida;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaId;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaRepository;

@Repository
public interface CompraDividaRepositoryJpa extends CompraDividaRepository, JpaRepository<CompraDivida, CompraDividaId> {

}
