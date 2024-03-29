package com.totvs.tj.qbank.domain.movimentacao;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE, force = true)
@AllArgsConstructor
@Entity
public class Transferencia {

    @Id
	private final TransferenciaId id;
    
    @OneToOne
	private final Movimento credito;
    
    @OneToOne
	private final Movimento debito;
    
    @Enumerated(STRING)
	private Situacao situacao;

	public static Builder builder() {
		return new Builder();
	}

	public Transferencia(Builder builder) {
		this.id = builder.id;
		this.credito = builder.credito;
		this.debito = builder.debito;
		this.situacao = Situacao.ABERTA;
	}

	public void transferir() {
	    debitar();
	    creditar();
	    finalizar();
	}

	private void debitar() {
		debito.processar();
	}

	private void creditar() {
		credito.processar();
	}

	public BigDecimal getValorDebito() {
		return debito.getValor();
	}

	public BigDecimal getValorCredito() {
		return credito.getValor();
	}

	public void finalizar() {
		this.debito.finalizar();
		this.credito.finalizar();

		this.situacao = Situacao.FINALIZADA;
	}

	public static class Builder {

		private TransferenciaId id;
		private Movimento credito;
		private Movimento debito;

		public Builder id(TransferenciaId id) {
			this.id = id;
			return this; 
		}

		public Builder credito(Movimento credito) {
			this.credito = credito;
			return this;
		}

		public Builder debito(Movimento debito) {
			this.debito = debito;
			return this;
		}

		public Transferencia build() {
			return new Transferencia(this);
		}

	}

	public static enum Situacao {
		ABERTA, FINALIZADA
	}
}
