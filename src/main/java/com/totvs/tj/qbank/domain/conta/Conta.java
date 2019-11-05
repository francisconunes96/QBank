package com.totvs.tj.qbank.domain.conta;

import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity
public class Conta {
    private static final BigDecimal VINTE_CINCO_PORCENTO = BigDecimal.valueOf(25).divide(BigDecimal.valueOf(100));
    
    @EmbeddedId
    private final ContaId id;
    private final EmpresaId empresa;
    private BigDecimal saldo;
    private BigDecimal limite;
    private LimiteEmergencial solicitouLimiteEmergencial;
    private Situacao situacao;

    public boolean aumentarLimite() {
        if (this.limiteEmergencialJaSolicitado()) {
            return false;
        }

        this.limite = this.limite.add(limite.divide(BigDecimal.valueOf(2)));
        this.solicitouLimiteEmergencial = LimiteEmergencial.SOLICITADO;
        return true;
    }
    
    public boolean limiteEmergencialJaSolicitado() {
        return LimiteEmergencial.SOLICITADO.equals(this.solicitouLimiteEmergencial);
    }

    public void suspender() {
        this.situacao = Situacao.SUSPENSA;
    }

    public boolean isDisponivel() {
        return Situacao.DISPONIVEL.equals(this.situacao);
    }

    public void creditar(BigDecimal valorCredito) {
        this.saldo = this.saldo.add(valorCredito);
    }
    
    public void debitar(BigDecimal valorDebito) {
        this.saldo = this.saldo.subtract(valorDebito);
    }

    public boolean estaDentroDoLimite(BigDecimal valorSaida) {
        return verificaLimite(valorSaida);
    }
    
    private boolean verificaLimite(BigDecimal valorSaida) {
        return verificaSeContemSaldoParaOperacaoSemUtilizarLimite(valorSaida) ? true : verificaSeContemSaldoParaOperacaoUtilizandoLimite(valorSaida);
    }

    private boolean verificaSeContemSaldoParaOperacaoUtilizandoLimite(BigDecimal valorSaida) {
        return valorPossivelDebitar().subtract(valorSaida).compareTo(BigDecimal.ZERO) >= 0;
    }

    private BigDecimal valorPossivelDebitar() {       
        return vinteCincoPorcentoPossivelDebitar().add(saldoCorrente());        
    }
    
    private BigDecimal saldoCorrente() {
        return verificaSeTemSaldo() ? this.saldo : BigDecimal.ZERO; 
    }
      
    private boolean verificaSeTemSaldo() {
        return this.saldo.compareTo(BigDecimal.ZERO) == 1;
    }

    private BigDecimal vinteCincoPorcentoPossivelDebitar() {
        return valorLimiteDisponivelParaSaida().multiply(VINTE_CINCO_PORCENTO);
    }

    private BigDecimal valorLimiteDisponivelParaSaida() {
        return verificaSeUtilizaLimite() ? valorLimiteDisponivel() : getLimite();
    }

    private boolean verificaSeUtilizaLimite() {
        return this.saldo.compareTo(BigDecimal.ZERO) < 0;
    }

    private BigDecimal valorLimiteDisponivel() {
        return getLimite().subtract(this.saldo.negate());
    }

    private boolean verificaSeContemSaldoParaOperacaoSemUtilizarLimite(BigDecimal valorSaida) {
        return this.saldo.subtract(valorSaida).compareTo(BigDecimal.ZERO) >= 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ContaId id;
        private Empresa empresa;
        private BigDecimal limite;

        public Builder id(ContaId id) {
            this.id = id;
            return this;
        }

        public Builder empresa(Empresa empresa) {
            this.empresa = empresa;
            return this;
        }

        public Builder calcularLimite() {
            this.limite = this.empresa.getValorMercado().divide(BigDecimal.valueOf(empresa.getQuantidadeFuncionarios()));

            if (this.limite.compareTo(BigDecimal.valueOf(15000)) == 1) {
                this.limite = BigDecimal.valueOf(15000);
            }

            return this;
        }

        public Conta build() {
            return new Conta(id, empresa.getId(), BigDecimal.ZERO, limite, LimiteEmergencial.NAO_SOLICITADO, Situacao.DISPONIVEL);
        }

    }

    public static enum Situacao {
        DISPONIVEL,
        SUSPENSA
    }
    
    private static enum LimiteEmergencial {
        SOLICITADO, NAO_SOLICITADO
    }

}
