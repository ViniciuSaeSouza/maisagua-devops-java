package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_historico_reservatorio")
@Data
public class HistoricoReservatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico", nullable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "nivel_litros", nullable = false)
    private Integer nivelLitros;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_reservatorio", nullable = false)
    private Reservatorio reservatorio;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_status_reservatorio")
    private StatusReservatorio status;
}
