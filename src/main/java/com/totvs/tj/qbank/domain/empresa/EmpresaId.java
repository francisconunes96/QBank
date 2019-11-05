package com.totvs.tj.qbank.domain.empresa;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
@Embeddable
public class EmpresaId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String value;

    public static EmpresaId generate() {
        return EmpresaId.from(UUID.randomUUID().toString());
    }

}
