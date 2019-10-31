package com.totvs.tj.qbank.domain.movimentacao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "from")
public class CompraDividaId {

    private String value;

    public static CompraDividaId generate() {
	return CompraDividaId.from(UUID.randomUUID().toString());
    }

}
