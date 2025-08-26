package br.com.fiap.mais_agua.model.DTO;

import br.com.fiap.mais_agua.model.StatusReservatorio;

public record HistoricoReservatorioDTO(
        Integer id,
        Integer nivelLitros,
        String data_hora,
        ReservatorioBasicoDTO reservatorio,
        UnidadeReadDTO unidade,
        StatusReservatorio status
) {}
