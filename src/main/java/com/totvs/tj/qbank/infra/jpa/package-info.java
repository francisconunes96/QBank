@TypeDef(name = "ContaId", typeClass = IdCustomTypes.Conta.class, defaultForType = ContaId.class)
@TypeDef(name = "EmprestimoId", typeClass = IdCustomTypes.Emprestimo.class, defaultForType = EmprestimoId.class)
@TypeDef(name = "CompraDividaId", typeClass = IdCustomTypes.CompraDivida.class, defaultForType = CompraDividaId.class)
@TypeDef(name = "TransferenciaId", typeClass = IdCustomTypes.Transferencia.class, defaultForType = TransferenciaId.class)
@TypeDef(name = "MovimentoId", typeClass = IdCustomTypes.Movimento.class, defaultForType = MovimentoId.class)
@TypeDef(name = "EmpresaId", typeClass = IdCustomTypes.Empresa.class, defaultForType = EmpresaId.class)
package com.totvs.tj.qbank.infra.jpa;

import org.hibernate.annotations.TypeDef;

import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaId;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;
