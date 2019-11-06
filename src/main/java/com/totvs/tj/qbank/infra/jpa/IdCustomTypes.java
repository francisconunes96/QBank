package com.totvs.tj.qbank.infra.jpa;

import com.totvs.tj.qbank.domain.conta.ContaId;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;
import com.totvs.tj.qbank.domain.movimentacao.CompraDividaId;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;
import com.totvs.tj.qbank.domain.movimentacao.TransferenciaId;

public interface IdCustomTypes {

    public final static class Conta extends IdCustomType<ContaId> {

        @Override
        public Class<ContaId> returnedClass() {
            return ContaId.class;
        }

        @Override
        public ContaId from(String value) {
            return ContaId.from(value);
        }
    }

    public final static class Emprestimo extends IdCustomType<EmprestimoId> {

        @Override
        public Class<EmprestimoId> returnedClass() {
            return EmprestimoId.class;
        }

        @Override
        public EmprestimoId from(String value) {
            return EmprestimoId.from(value);
        }
    }
    
    public final static class CompraDivida extends IdCustomType<CompraDividaId> {

        @Override
        public Class<CompraDividaId> returnedClass() {
            return CompraDividaId.class;
        }

        @Override
        public CompraDividaId from(String value) {
            return CompraDividaId.from(value);
        }
    }
    
    public final static class Transferencia extends IdCustomType<TransferenciaId> {

        @Override
        public Class<TransferenciaId> returnedClass() {
            return TransferenciaId.class;
        }

        @Override
        public TransferenciaId from(String value) {
            return TransferenciaId.from(value);
        }
    }
    
    public final static class Movimento extends IdCustomType<MovimentoId> {

        @Override
        public Class<MovimentoId> returnedClass() {
            return MovimentoId.class;
        }

        @Override
        public MovimentoId from(String value) {
            return MovimentoId.from(value);
        }
    }
    
    public final static class Empresa extends IdCustomType<EmpresaId> {

        @Override
        public Class<EmpresaId> returnedClass() {
            return EmpresaId.class;
        }

        @Override
        public EmpresaId from(String value) {
            return EmpresaId.from(value);
        }
    }

}
