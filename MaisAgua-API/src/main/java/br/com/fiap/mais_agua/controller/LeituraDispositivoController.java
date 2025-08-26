package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.LeituraDispositivo;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
import br.com.fiap.mais_agua.repository.LeituraDispositivoRepository;
import br.com.fiap.mais_agua.repository.ReservatorioDispositivoRepository;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.specification.LeituraDispositivoSpecification;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/leitura-dispositivo")
@Slf4j
@Tag(name = "Leituras de Dispositivos", description = "Leituras geradas automaticamente ou manualmente a partir dos dispositivos conectados aos reservatórios")
public class LeituraDispositivoController {

    @Autowired
    private LeituraDispositivoRepository leituraRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Autowired
    private ReservatorioDispositivoRepository reservatorioDispositivoRepository;
    @Autowired
    private ReservatorioRepository reservatorioRepository;

    public record LeituraDispositivoFilter(Integer idReservatorio) {}

    @GetMapping
    @Operation(
            summary = "Listar leituras dos dispositivos",
            description = "Retorna uma lista paginada de leituras com filtro opcional de ID do reservatório.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Leituras encontradas com sucesso"),
                    @ApiResponse(responseCode = "403", description = "O usuário autenticado não tem permissão para acessar o reservatório informado.")
            }
    )
    @Cacheable("leituraDispositivo")
    public Page<LeituraDispositivo> index(
            @AuthenticationPrincipal Usuario usuario,
            @ParameterObject LeituraDispositivoFilter filters,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (filters.idReservatorio() != null) {
            boolean pertence = reservatorioRepository.existsByIdReservatorioAndUnidadeUsuario(
                    filters.idReservatorio(), usuario
            );

            if (!pertence) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem acesso a esse reservatório");
            }
        }
        Specification<LeituraDispositivo> spec = LeituraDispositivoSpecification.withFilters(filters, usuario);

        return leituraRepository.findAll(spec, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cadastrar nova leitura",
            description = "Registra uma nova leitura (nível, turbidez e pH) para um dispositivo, validando se ele pertence ao usuário.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Leitura registrada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado ao dispositivo"),
                    @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado")
            }
    )
    @CacheEvict(value = "leituraDispositivo", allEntries = true)
    public LeituraDispositivo create(@RequestBody @Valid LeituraDispositivo leitura,
                                     @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando leitura de dispositivo");
        Dispositivo dispositivo = getDispositivoDoUsuario(leitura.getDispositivo().getIdDispositivo(), usuario);
        leitura.setDispositivo(dispositivo);
        return leituraRepository.save(leitura);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar leitura por ID",
            description = "Busca uma leitura específica de um dispositivo, validando a permissão do usuário.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Leitura encontrada"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
            }
    )
    public ResponseEntity<LeituraDispositivo> get(@PathVariable Integer id,
                                                  @AuthenticationPrincipal Usuario usuario) {
        var leitura = getLeituraDoUsuario(id, usuario);
        return ResponseEntity.ok(leitura);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Excluir leitura",
            description = "Remove uma leitura de dispositivo, se ela pertencer ao usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Leitura excluída com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
            }
    )
    @CacheEvict(value = "leituraDispositivo", allEntries = true)
    public ResponseEntity<Object> destroy(@PathVariable Integer id,
                                          @AuthenticationPrincipal Usuario usuario) {
        var leitura = getLeituraDoUsuario(id, usuario);
        leituraRepository.delete(leitura);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Atualizar leitura",
            description = "Atualiza os dados de uma leitura do dispositivo, mantendo data/hora original e validando permissões.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Leitura atualizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Leitura ou dispositivo não encontrado")
            }
    )
    @CacheEvict(value = "leituraDispositivo", allEntries = true)
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid LeituraDispositivo leitura,
                                         @AuthenticationPrincipal Usuario usuario) {
        var leituraExistente = getLeituraDoUsuario(id, usuario);
        Dispositivo dispositivo = getDispositivoDoUsuario(leitura.getDispositivo().getIdDispositivo(), usuario);
        leitura.setDispositivo(dispositivo);
        BeanUtils.copyProperties(leitura, leituraExistente, "id_leitura", "dataHora");
        leituraRepository.save(leituraExistente);
        return ResponseEntity.ok(leituraExistente);
    }

    private LeituraDispositivo getLeituraDoUsuario(Integer id, Usuario usuario) {
        var leitura = leituraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leitura não encontrada"));

        if (!pertenceAoUsuario(leitura.getDispositivo(), usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem acesso a essa leitura");
        }

        return leitura;
    }

    private Dispositivo getDispositivoDoUsuario(Integer id, Usuario usuario) {
        var dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));

        if (!pertenceAoUsuario(dispositivo, usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem acesso a esse dispositivo");
        }

        return dispositivo;
    }

    private boolean pertenceAoUsuario(Dispositivo dispositivo, Usuario usuario) {
        return reservatorioDispositivoRepository.findByDispositivo(dispositivo).stream()
                .anyMatch(reservatorioDispositivo ->
                        reservatorioDispositivo.getReservatorio()
                                .getUnidade()
                                .getUsuario()
                                .equals(usuario)
                );
    }
}
