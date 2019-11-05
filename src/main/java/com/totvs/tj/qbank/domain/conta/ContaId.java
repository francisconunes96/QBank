package com.totvs.tj.qbank.domain.conta;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
@Embeddable
public class ContaId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Column(name="conta_id")
    private String value;
        
    public static ContaId generate() {
        return ContaId.from(UUID.randomUUID().toString());
    }
    
}
