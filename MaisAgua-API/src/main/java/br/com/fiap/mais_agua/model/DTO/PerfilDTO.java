package br.com.fiap.mais_agua.model.DTO;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PerfilDTO (
        String nome,
        String logradouro,
        Integer numero,
        Integer nivelLitros,
        BigDecimal ph,
        Integer nivelPct
){
}
