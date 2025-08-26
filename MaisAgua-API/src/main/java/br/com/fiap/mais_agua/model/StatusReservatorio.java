package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "tbl_status_reservatorio")
@Data
public class StatusReservatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_reservatorio",nullable = false, updatable = false)
    private Integer id;

    @NotBlank
    private String status;
}
