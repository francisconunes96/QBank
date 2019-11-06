package com.totvs.tj.qbank.domain.movimentacao;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(access = PRIVATE, force = true)
@AllArgsConstructor(staticName="from")
@Embeddable
public class EmprestimoId implements Serializable {
    private static final long serialVersionUID = 1L;
	
    @Column(name="emprestimo_id")
	private String value;
	
	public static EmprestimoId generate() {
		return EmprestimoId.from(UUID.randomUUID().toString());
	}
	
}
