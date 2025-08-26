package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_cidade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cidade", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "nome_cidade", nullable = false, unique = true)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
}