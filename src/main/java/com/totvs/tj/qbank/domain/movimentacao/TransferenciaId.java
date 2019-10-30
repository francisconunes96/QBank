package com.totvs.tj.qbank.domain.movimentacao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
public class TransferenciaId {
    
    private String value;
    
    @Override
    public String toString() {
        return value;
    }
    
    public static TransferenciaId generate() {
        return TransferenciaId.from(UUID.randomUUID().toString());
    }
    
}

