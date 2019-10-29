package com.totvs.tj.qbank.domain.conta;

import java.math.BigDecimal;

import com.totvs.tj.qbank.domain.empresa.Empresa;
import com.totvs.tj.qbank.domain.empresa.EmpresaId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Conta {
    private static final BigDecimal VINTE_CINCO_PORCENTO = BigDecimal.valueOf(25).multiply(BigDecimal.valueOf(100));
    private final ContaId id;
    private final EmpresaId empresa;
    private BigDecimal saldo;
    private BigDecimal limite;
    private BigDecimal limiteEmergencial;
    private Situacao situacao;

    public boolean aumentarLimite() {
        if (this.limiteEmergencial.compareTo(BigDecimal.ZERO) == 1) {
            return false;
        }

        this.limiteEmergencial = this.limite.divide(BigDecimal.valueOf(2));
        return true;
    }

    public void suspender() {
        this.situacao = Situacao.SUSPENSA;
    }

    public boolean isDisponivel() {
        return Situacao.DISPONIVEL.equals(this.situacao);
    }

    public boolean creditar(BigDecimal valorCredito) {
        this.saldo = this.saldo.add(valorCredito);
        return true;
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
        return vinteCincoPorcentoPossivelDebitar().add(this.saldo);
    }

    private BigDecimal vinteCincoPorcentoPossivelDebitar() {
        return valorLimiteDisponivelParaSaida().divide(VINTE_CINCO_PORCENTO);
    }

    private BigDecimal valorLimiteDisponivelParaSaida() {
        return verificaSeUtilizaLimite() ? valorLimiteDisponivel() : getLimiteTotal();
    }

    private boolean verificaSeUtilizaLimite() {
        return this.saldo.compareTo(BigDecimal.ZERO) < 0;
    }

    private BigDecimal valorLimiteDisponivel() {
        return getLimiteTotal().subtract(this.saldo);
    }

    private boolean verificaSeContemSaldoParaOperacaoSemUtilizarLimite(BigDecimal valor) {
        return this.saldo.subtract(valor).compareTo(BigDecimal.ZERO) >= 0;
    }

    public BigDecimal getLimiteTotal() {
        return this.limite.add(this.limiteEmergencial);
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
            return new Conta(id, empresa.getId(), BigDecimal.ZERO, limite, BigDecimal.ZERO, Situacao.DISPONIVEL);
        }

    }

    public static enum Situacao {
        DISPONIVEL,
        SUSPENSA
    }

}
