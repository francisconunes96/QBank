package com.totvs.tj.qbank.domain.movimentacao;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
@Embeddable
public class CompraDividaId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String value;

    public static CompraDividaId generate() {
	return CompraDividaId.from(UUID.randomUUID().toString());
    }

}
