package br.com.fiap.mais_agua.model.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroDTO(
        @NotBlank(message = "campo obrigatório")
        String nome,
        @Email(message = "email inválido")
        @NotBlank(message = "campo obrigatório")
        String email,
        @Size(min = 5)
        String senha
) {}
