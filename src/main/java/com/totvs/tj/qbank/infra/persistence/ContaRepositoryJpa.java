package com.totvs.tj.qbank.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totvs.tj.qbank.domain.conta.Conta;
import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.conta.ContaRepository;

@Repository
public interface ContaRepositoryJpa extends ContaRepository, JpaRepository<Conta, ContaId> {

}