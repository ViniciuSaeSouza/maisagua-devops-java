package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_reservatorio_dispositivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservatorioDispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservatorio_dispositivo", nullable = false, updatable = false)
    private Integer idReservatorioDispositivo;

    @Column(name = "data_instalacao", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataInstalacao= LocalDateTime.now();

    @Column(name = "data_remocao")
    private LocalDate dataRemocao;

    @ManyToOne
    @JoinColumn(name = "id_reservatorio", nullable = false)
    private Reservatorio reservatorio;

    @ManyToOne
    @JoinColumn(name = "id_dispositivo")
    private Dispositivo dispositivo;
}
