package br.com.fiap.mais_agua.model.DTO;

public record ReservatorioReadDTO(
        Integer idReservatorio,
        String nomeReservatorio,
        Integer capacidadeTotalLitros,
        String dataInstalacao,

        UnidadeReadDTO unidade
) {}
