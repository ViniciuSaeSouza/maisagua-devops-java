package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_pais")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pais", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "nome_pais", nullable = false, unique = true)
    private String nome;
}
