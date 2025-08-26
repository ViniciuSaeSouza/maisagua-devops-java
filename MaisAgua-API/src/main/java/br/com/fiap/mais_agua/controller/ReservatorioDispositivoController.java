package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.model.DTO.ReservatorioBasicoDTO;
import br.com.fiap.mais_agua.model.DTO.ReservatorioDispositivoDTO;
import br.com.fiap.mais_agua.model.DTO.UnidadeReadDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.ReservatorioDispositivoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservatorio-dispositivo")
@Slf4j
@Tag(name = "Dispositivo")
public class ReservatorioDispositivoController {
    @Autowired
    private ReservatorioDispositivoRepository reservatorioSensorRepository;

    @Autowired
    private ReservatorioRepository reservatorioRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @GetMapping
    @Operation(
            summary = "Listar vínculos entre reservatórios e dispositivos",
            description = "Retorna todos os vínculos entre reservatórios e dispositivos do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @Cacheable("reservatorioDispositivo")
    public List<ReservatorioDispositivoDTO> index(@AuthenticationPrincipal Usuario usuario) {
        return reservatorioSensorRepository.findAll().stream()
                .filter(rs -> rs.getReservatorio().getUnidade().getUsuario().equals(usuario))
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar vínculo entre reservatório e dispositivo",
            description = "Cria um novo vínculo manualmente entre um reservatório e um dispositivo existente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Vínculo criado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Reservatório ou dispositivo não encontrado")
            }
    )
    @CacheEvict(value = "reservatorioDispositivo", allEntries = true)
    public ReservatorioDispositivoDTO create(@RequestBody @Valid ReservatorioDispositivo reservatorioSensor,
                                             @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando ReservatorioSensor");

        Reservatorio reservatorio = getReservatorio(reservatorioSensor.getReservatorio().getIdReservatorio(), usuario);
        Dispositivo dispositivo = getDispositivo(reservatorioSensor.getDispositivo().getIdDispositivo());

        reservatorioSensor.setReservatorio(reservatorio);
        reservatorioSensor.setDispositivo(dispositivo);

        var saved = reservatorioSensorRepository.save(reservatorioSensor);
        return toDTO(saved);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar vínculo por ID",
            description = "Retorna os dados de um vínculo entre reservatório e dispositivo pelo ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vínculo encontrado"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Vínculo não encontrado")
            }
    )
    public ResponseEntity<ReservatorioDispositivoDTO> get(@PathVariable Integer id,
                                                          @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(toDTO(getReservatorioSensor(id, usuario)));
    }


    @DeleteMapping("{id}")
    @Operation(
            summary = "Excluir vínculo",
            description = "Remove um vínculo entre reservatório e dispositivo, se pertencer ao usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Vínculo removido com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Vínculo não encontrado")
            }
    )
    @CacheEvict(value = "reservatorioDispositivo", allEntries = true)
    public ResponseEntity<Object> destroy(@PathVariable Integer id,
                                          @AuthenticationPrincipal Usuario usuario) {
        var reservatorioSensor = getReservatorioSensor(id, usuario);
        reservatorioSensorRepository.delete(reservatorioSensor);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Atualizar vínculo",
            description = "Atualiza o vínculo entre reservatório e dispositivo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vínculo atualizado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Reservatório ou dispositivo não encontrado")
            }
    )
    @CacheEvict(value = "reservatorioDispositivo", allEntries = true)
    public ResponseEntity<ReservatorioDispositivoDTO> update(@PathVariable Integer id,
                                                             @RequestBody @Valid ReservatorioDispositivo reservatorioSensor,
                                                             @AuthenticationPrincipal Usuario usuario) {
        var oldRS = getReservatorioSensor(id, usuario);

        Reservatorio reservatorio = getReservatorio(reservatorioSensor.getReservatorio().getIdReservatorio(), usuario);
        Dispositivo dispositivo = getDispositivo(reservatorioSensor.getDispositivo().getIdDispositivo());

        reservatorioSensor.setReservatorio(reservatorio);
        reservatorioSensor.setDispositivo(dispositivo);

        BeanUtils.copyProperties(reservatorioSensor, oldRS, "idReservatorioDispositivo");
        reservatorioSensorRepository.save(oldRS);

        return ResponseEntity.ok(toDTO(oldRS));
    }


    // Método auxiliar para conversão de entidade para DTO
    private ReservatorioDispositivoDTO toDTO(ReservatorioDispositivo entity) {
        var unidade = entity.getReservatorio().getUnidade();
        var usuario = unidade.getUsuario();

        return new ReservatorioDispositivoDTO(
                entity.getIdReservatorioDispositivo(),
                entity.getDataInstalacao() != null ? entity.getDataInstalacao().toString() : null,
                new ReservatorioBasicoDTO(
                        entity.getReservatorio().getIdReservatorio(),
                        entity.getReservatorio().getNome(),
                        entity.getReservatorio().getCapacidadeTotalLitros()
                ),
                UnidadeReadDTO.builder()
                        .idUnidade(unidade.getIdUnidade())
                        .nomeUnidade(unidade.getNome())
                        .capacidadeTotalLitros(unidade.getCapacidadeTotalLitros())
                        .dataCadastro(unidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .usuario(new UsuarioResponseDTO(
                                usuario.getIdUsuario(),
                                usuario.getNome(),
                                usuario.getEmail()
                        ))
                        .build()
        );
    }

    private ReservatorioDispositivo getReservatorioSensor(Integer id, Usuario usuario) {
        var rs = reservatorioSensorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatorio-Sensor não encontrado"));

        if (!rs.getReservatorio().getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este sensor");
        }
        return rs;
    }


    private Reservatorio getReservatorio(Integer id, Usuario usuario) {
        var r = reservatorioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatorio não encontrado"));

        if (!r.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este reservatório");
        }
        return r;
    }


    private Dispositivo getDispositivo(Integer id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
    }
}
