package br.com.fiap.mais_agua.controller;
import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.StatusReservatorio;
import br.com.fiap.mais_agua.repository.StatusReservatorioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/status-reservatorio")
@Slf4j
@Tag(name = "Status do Reservatório", description = "Operações para consulta dos status que representam a condição do reservatório")
public class StatusReservatorioController {

    @Autowired
    private StatusReservatorioRepository repository;

    @GetMapping
    @Operation(
            summary = "Listar todos os status",
            description = "Retorna a lista de todos os status disponíveis para representar o nível do reservatório.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status retornados com sucesso")
            }
    )
    @Cacheable("statusReservatorio")
    public List<StatusReservatorio> index() {
        return repository.findAll();
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar status por ID",
            description = "Retorna os dados de um status específico identificado pelo ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status encontrado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Status não encontrado")
            }
    )
    public ResponseEntity<StatusReservatorio> get(@PathVariable Integer id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status não encontrado"));
        return ResponseEntity.ok(getStatus(id));
    }


    private StatusReservatorio getStatus(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Status não encontrado"));
    }

}
