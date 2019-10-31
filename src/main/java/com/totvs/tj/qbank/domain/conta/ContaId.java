package com.totvs.tj.qbank.domain.conta;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
public class ContaId {
    
    private String value;
        
    public static ContaId generate() {
        return ContaId.from(UUID.randomUUID().toString());
    }
    
}
