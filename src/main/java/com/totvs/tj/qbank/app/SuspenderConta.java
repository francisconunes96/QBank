package com.totvs.tj.qbank.app;

import com.totvs.tj.qbank.domain.conta.ContaId;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName="from")
public class SuspenderConta {
    
    private ContaId conta ;

}
