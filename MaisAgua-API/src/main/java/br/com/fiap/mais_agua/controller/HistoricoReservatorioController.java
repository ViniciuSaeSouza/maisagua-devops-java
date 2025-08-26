package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.*;
import br.com.fiap.mais_agua.model.DTO.*;
import br.com.fiap.mais_agua.repository.HistoricoReservatorioRepository;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.StatusReservatorioRepository;
import br.com.fiap.mais_agua.specification.HistoricoReservatorioSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/historico-reservatorio")
@Slf4j
@Tag(name = "Histórico do Reservatório", description = "Operações relacionadas ao monitoramento do nível de água nos reservatórios")
public class HistoricoReservatorioController {

    @Autowired
    private HistoricoReservatorioRepository historicoRepository;

    @Autowired
    private ReservatorioRepository reservatorioRepository;

    @Autowired
    private StatusReservatorioRepository statusRepository;

    public record HistoricoReservatorioFilters(Integer idReservatorio, Integer nivelLitros, Integer status){}

    @GetMapping
    @Operation(
            summary = "Listar históricos",
            description = "Retorna todos os registros de histórico dos reservatórios do usuário logado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Históricos retornados com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Você não tem acesso a este reservatório")
            }
    )
    @Cacheable("historicoReservatorio")
    public List<HistoricoReservatorioDTO> index(
            @AuthenticationPrincipal Usuario usuario,
            @ParameterObject HistoricoReservatorioFilters filters) {

        if (filters.idReservatorio != null) {
            boolean pertence = reservatorioRepository.existsByIdReservatorioAndUnidadeUsuario(
                    filters.idReservatorio, usuario
            );

            if (!pertence) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem acesso a este reservatório");
            }
        }
        var specification = HistoricoReservatorioSpecification.withFilters(filters, usuario);

        List<HistoricoReservatorio> historicos = historicoRepository.findAll(specification);

        return historicos.stream()
                .map(this::toDTO)
                .toList();
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar histórico manualmente",
            description = "Registra manualmente o nível de água de um reservatório, vinculando a um status.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Histórico criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Nível de litros maior que a capacidade"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Reservatório ou status não encontrados")
            }
    )
    @CacheEvict(value = "historicoReservatorio", allEntries = true)
    public HistoricoReservatorioDTO create(@RequestBody @Valid HistoricoReservatorio historico,
                                           @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando histórico");

        Reservatorio reservatorio = getReservatorio(historico.getReservatorio().getIdReservatorio(), usuario);
        validarNivelLitros(historico.getNivelLitros(), reservatorio.getCapacidadeTotalLitros());

        StatusReservatorio status = getStatus(historico.getStatus().getId());

        historico.setReservatorio(reservatorio);
        historico.setStatus(status);

        var saved = historicoRepository.save(historico);
        return toDTO(saved);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar histórico por ID",
            description = "Retorna um registro de histórico específico vinculado ao usuário.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Histórico não encontrado")
            }
    )
    public ResponseEntity<HistoricoReservatorioDTO> get(@PathVariable Integer id,
                                                        @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(toDTO(getHistorico(id, usuario)));
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Excluir histórico",
            description = "Remove um registro de histórico do reservatório vinculado ao usuário.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Histórico excluído com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Histórico não encontrado")
            }
    )
    @CacheEvict(value = "historicoReservatorio", allEntries = true)
    public ResponseEntity<Object> destroy(@PathVariable Integer id,
                                          @AuthenticationPrincipal Usuario usuario) {
        var historico = getHistorico(id, usuario);

        historicoRepository.delete(historico);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Atualizar histórico",
            description = "Atualiza os dados de um histórico já existente de um usuário autenticado, respeitando a capacidade do reservatório.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Histórico atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Nível de litros inválido"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Histórico ou status não encontrados")
            }
    )
    @CacheEvict(value = "historicoReservatorio", allEntries = true)
    public ResponseEntity<HistoricoReservatorioDTO> update(@PathVariable Integer id,
                                                           @RequestBody @Valid HistoricoReservatorio historico,
                                                           @AuthenticationPrincipal Usuario usuario) {
        var historicoDB = getHistorico(id, usuario);

        Reservatorio reservatorio = getReservatorio(historico.getReservatorio().getIdReservatorio(), usuario);
        validarNivelLitros(historico.getNivelLitros(), reservatorio.getCapacidadeTotalLitros());

        StatusReservatorio status = getStatus(historico.getStatus().getId());

        historico.setReservatorio(reservatorio);
        historico.setStatus(status);

        BeanUtils.copyProperties(historico, historicoDB, "id");
        historicoRepository.save(historicoDB);

        return ResponseEntity.ok(toDTO(historicoDB));
    }

    private void validarNivelLitros(int nivelLitros, int capacidadeTotalLitros) {
        if (nivelLitros > capacidadeTotalLitros) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O nível de litros não pode ser maior que a capacidade total do reservatório.");
        }
    }

    private HistoricoReservatorioDTO toDTO(HistoricoReservatorio entity) {
        var unidade = entity.getReservatorio().getUnidade();
        var usuario = unidade.getUsuario();

        return new HistoricoReservatorioDTO(
                entity.getId(),
                entity.getNivelLitros(),
                entity.getDataHora() != null ? entity.getDataHora().toString() : null,
                new ReservatorioBasicoDTO(
                        entity.getReservatorio().getIdReservatorio(),
                        entity.getReservatorio().getNome(),
                        entity.getReservatorio().getCapacidadeTotalLitros()
                ),
                UnidadeReadDTO.builder()
                        .idUnidade(unidade.getIdUnidade())
                        .nomeUnidade(unidade.getNome())
                        .capacidadeTotalLitros(unidade.getCapacidadeTotalLitros())
                        .dataCadastro(unidade.getDataCadastro().toString())
                        .usuario(new UsuarioResponseDTO(
                                usuario.getIdUsuario(),
                                usuario.getNome(),
                                usuario.getEmail()
                        ))
                        .build(),
                entity.getStatus()
        );
    }

    private HistoricoReservatorio getHistorico(Integer id, Usuario usuario) {
        var historico = historicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Histórico não encontrado"));

        if (!historico.getReservatorio().getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este histórico");
        }

        return historico;
    }

    private Reservatorio getReservatorio(Integer id, Usuario usuario) {
        var reservatorio = reservatorioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatório não encontrado"));

        if (!reservatorio.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este reservatório");
        }

        return reservatorio;
    }

    private StatusReservatorio getStatus(Integer id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status não encontrado"));
    }
}
