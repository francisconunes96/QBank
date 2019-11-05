package com.totvs.tj.qbank.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totvs.tj.qbank.domain.movimentacao.Emprestimo;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoRepository;

@Repository
public interface EmprestimoRepositoryJpa extends EmprestimoRepository, JpaRepository<Emprestimo, EmprestimoId> {

}
