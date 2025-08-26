package br.com.fiap.mais_agua.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_leitura_dispositivo")
public class LeituraDispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_leitura", nullable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "nivel_pct", nullable = false)
    private Integer nivelPct;

    @NotNull
    @Column(name = "turbidez_ntu", nullable = false)
    private Integer turbidezNtu;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("14.00")
    @Column(name = "ph_int", precision = 4, scale = 2, nullable = false)
    private BigDecimal phInt;


    @Column(name = "data_hora", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataHora = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_dispositivo")
    @JsonIgnoreProperties("leituraDispositivo")
    private Dispositivo dispositivo;
}
