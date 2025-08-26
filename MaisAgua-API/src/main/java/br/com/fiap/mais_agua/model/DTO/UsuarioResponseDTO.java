package br.com.fiap.mais_agua.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String nome;
    private String email;
}
