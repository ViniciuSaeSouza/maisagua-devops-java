package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_endereco")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco", nullable = false, updatable = false)
    private Integer id;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 50, message = "Logradouro deve ter no máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String logradouro;

    @NotNull(message = "Número é obrigatório")
    @Min(value = 1, message = "Número deve ser maior que zero")
    @Column(nullable = false)
    private Integer numero;

    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String complemento;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos numéricos")
    @Column(nullable = false, length = 8)
    private String cep;

    @NotNull(message = "Cidade é obrigatória")
    @ManyToOne
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;

    @NotNull(message = "Unidade é obrigatória")
    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;
}
