package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_unidade")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidade", nullable = false, updatable = false)
    private Integer idUnidade;

    @NotBlank(message = "Campo obrigatório")
    private String nome;

    @NotNull(message = "Campo obrigatório")
    @Column(name = "capacidade_total_litros", nullable = false)
    private Integer capacidadeTotalLitros;

    @Column(name = "data_cadastro", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;
}


