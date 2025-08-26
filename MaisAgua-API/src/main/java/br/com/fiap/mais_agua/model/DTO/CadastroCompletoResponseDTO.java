package br.com.fiap.mais_agua.model.DTO;

import lombok.Builder;

@Builder
public record CadastroCompletoResponseDTO(
        Integer idUsuario,
        String nomeUsuario,
        String email,
        String nomeUnidade,
        Integer capacidadeTotalLitros,
        String logradouro,
        Integer numero,
        String complemento,
        String cep,
        Integer idCidade
) {}
