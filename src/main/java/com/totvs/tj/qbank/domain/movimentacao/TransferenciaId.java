package com.totvs.tj.qbank.domain.movimentacao;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
@Embeddable
public class TransferenciaId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String value;
        
    public static TransferenciaId generate() {
        return TransferenciaId.from(UUID.randomUUID().toString());
    }
    
}

