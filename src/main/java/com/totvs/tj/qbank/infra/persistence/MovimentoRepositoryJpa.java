package com.totvs.tj.qbank.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoRepository;

@Repository
public interface MovimentoRepositoryJpa extends MovimentoRepository, JpaRepository<Movimento, MovimentoId> {

}