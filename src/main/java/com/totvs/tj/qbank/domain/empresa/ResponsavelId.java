package com.totvs.tj.qbank.domain.empresa;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
public class ResponsavelId {
    
    private String value;
    
    @Override
    public String toString() {
        return value;
    }
    
    public static ResponsavelId generate() {
        return ResponsavelId.from(UUID.randomUUID().toString());
    }
    
}
