package br.com.fiap.mais_agua.model.DTO;

public record UnidadeResponseDTO (
        String nome,
        Integer capacidade_total_litros,
        Integer idUsuario
) {}