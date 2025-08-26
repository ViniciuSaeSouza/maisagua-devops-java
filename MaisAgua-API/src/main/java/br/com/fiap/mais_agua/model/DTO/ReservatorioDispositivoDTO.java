package br.com.fiap.mais_agua.model.DTO;

public record ReservatorioDispositivoDTO(
        Integer id,
        String dataInstalacao,
        ReservatorioBasicoDTO reservatorio,
        UnidadeReadDTO unidade

) {}
