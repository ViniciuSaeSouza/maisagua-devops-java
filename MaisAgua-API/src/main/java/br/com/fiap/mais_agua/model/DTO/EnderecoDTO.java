package br.com.fiap.mais_agua.model.DTO;

public record EnderecoDTO(
        Integer id,
        String logradouro,
        Integer numero,
        String complemento,
        String cep,
        String pais,
        String estado,
        String cidade,
        UnidadeReadDTO unidade
) {}
