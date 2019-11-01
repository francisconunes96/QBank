package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.movimentacao.Movimento;
import com.totvs.tj.qbank.domain.movimentacao.MovimentoRepository;

public class MovimentoService {
	
	private MovimentoRepository repository;

	public MovimentoService(MovimentoRepository repository) {
		this.repository = repository;
	}
	
	public Movimento handle(SolicitacaoAprovacaoGerente cmd) {

		Movimento movimento = cmd.getMovimento();

		if (cmd.isAprovada()) {
			movimento.aprovar();
		} else {
			movimento.recusar();
		}

		return movimento;
	}
	
	
}
