package br.com.fiap.mais_agua.model.DTO;

import lombok.Builder;

@Builder
public record UnidadeReadDTO(
        Integer idUnidade,
        String nomeUnidade,
        Integer capacidadeTotalLitros,
        String dataCadastro,
        UsuarioResponseDTO usuario
) {}