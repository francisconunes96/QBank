package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Emprestimo;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoId;
import com.totvs.tj.qbank.domain.movimentacao.EmprestimoRepository;
import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoId;

public class EmprestimoService {
    
    private EmprestimoRepository repository;

    public EmprestimoService (EmprestimoRepository repository) {
        this.repository = repository;
    }
		
	public Emprestimo handle(SolicitacaoEmprestimo cmd) {

	    Movimento movimento = Movimento.builder()
	                .conta(cmd.getConta())
	                .emprestimo()
	                .credito()
	                .valor(cmd.getValor())
	                .id(MovimentoId.generate())
	            .build();
	    
	    Emprestimo emprestimo = Emprestimo.builder()
	                .id(EmprestimoId.generate())
	                .movimento(movimento)
	            .build();

		SolicitacaoVerificacaoSaldo verificacaoSaldo = SolicitacaoVerificacaoSaldo.of(movimento);
		
		VerificacaoLimiteService service = new VerificacaoLimiteService();

		ResultadoVerificacaoSaldo resultado = service.handle(verificacaoSaldo);

		if (SaldoExcedido.class.equals(resultado.getClass())) {
			emprestimo.aguardarAprovacao();
		}else {
		    emprestimo.emprestar();
		}
		
		repository.save(emprestimo);
		
		return emprestimo;		
	}

	public Emprestimo handle(SolicitacaoAprovacaoEmprestimo cmd) {

		Emprestimo emprestimo = repository.getOne(cmd.getEmprestimoId());

		if (cmd.isAprovada()) {
		    emprestimo.emprestar();
		} else {
			emprestimo.recusar();
		}
		
		repository.save(emprestimo);

		return emprestimo;
	}

    public Emprestimo handle(SolicitacaoQuitacaoDivida cmd) throws Exception {
        
        Emprestimo emprestimo = repository.getOne(cmd.getEmprestimoId());
        
        SolicitacaoVerificacaoSaldo verificacaoSaldo = SolicitacaoVerificacaoSaldo.of(emprestimo.getMovimento());
        
        VerificacaoLimiteService service = new VerificacaoLimiteService();

        ResultadoVerificacaoSaldo resultado = service.handle(verificacaoSaldo);
        
        if(SaldoExcedido.class.equals(resultado.getClass())) {
            throw new Exception("Conta não apresenta saldo para quitar divida.");
        }
        
        // TODO: Criar movimento de débito
        emprestimo.quitar();
        repository.save(emprestimo);
        
        return emprestimo;
    }
	
}
