package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/dispositivo")
@Slf4j
@Tag(name = "Dispositivo", description = "Operações relacionadas a dispositivos (ESP32 acoplado com sensores de: PH, Turbidez, Nível da água) no sistema")
public class DispositivoController {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @GetMapping
    @Operation(
            summary = "Listar dispositivos",
            description = "Retorna todos os dispositivos cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @Cacheable("dispositivos")
    public List<Dispositivo> index() {
        return dispositivoRepository.findAll();
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar dispositivo por ID",
            description = "Retorna os dados de um dispositivo específico pelo ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dispositivo encontrado"),
                    @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado")
            }
    )
    public ResponseEntity<Dispositivo> get(@PathVariable Integer id) {
        log.info("Buscando dispositivo com o id " + id);
        return ResponseEntity.ok(getDispositivo(id));
    }

    private Dispositivo getDispositivo(Integer id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
    }
}
