package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "nome_estado", nullable = false, unique = true)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_pais")
    private Pais pais;
}