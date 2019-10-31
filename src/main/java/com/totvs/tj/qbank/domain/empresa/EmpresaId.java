package com.totvs.tj.qbank.domain.empresa;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
public class EmpresaId {
    
    private String value;
        
    public static EmpresaId generate() {
        return EmpresaId.from(UUID.randomUUID().toString());
    }
    
}
