package com.totvs.tj.qbank.domain.movimentacao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Transferencia {

       private Movimento credito;
       private Movimento debito;
       private Situacao situacao;
       private TransferenciaId id;
       
       public static enum Situacao {
           ABERTA, FINALIZADA
       }
       
       
       public static Builder builder() {
           return new Builder();
       }
       
       public static class Builder {
           private Movimento credito;
           private Movimento debito;
           private Situacao situacao;
           private TransferenciaId id;
           
           
       }
       
       
       
}
