package com.totvs.tj.qbank.domain.movimentacao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
public class MovimentoId {
    
    private String value;
    
    @Override
    public String toString() {
        return value;
    }
    
    public static MovimentoId generate() {
        return MovimentoId.from(UUID.randomUUID().toString());
    }
    
}