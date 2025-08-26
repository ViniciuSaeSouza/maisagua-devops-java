package br.com.fiap.mais_agua.model.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CadastroCompletoDTO(
        // Dados do Usuário
        @NotBlank(message = "campo obrigatório")
        String nomeUsuario,
        @Email(message = "email inválido")
        @NotBlank(message = "campo obrigatório")
        String email,
        @Size(min = 5)
        String senha,

        // Dados da Unidade
        @NotBlank(message = "Campo obrigatório")

        @NotBlank String nomeUnidade,
        @NotNull(message = "Campo obrigatório")
        Integer capacidadeTotalLitros,

        // Dados do Endereço
        @NotBlank(message = "Campo obrigatório")
        String logradouro,
        @NotNull(message = "Campo obrigatório")
        Integer numero,
        String complemento,

        @NotBlank(message = "Campo obrigatório")
        String cep,
        @NotNull(message = "Campo obrigatório")
        Integer idCidade
) {}