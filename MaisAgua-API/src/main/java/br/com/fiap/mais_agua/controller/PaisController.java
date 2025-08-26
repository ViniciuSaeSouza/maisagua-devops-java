package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Pais;
import br.com.fiap.mais_agua.repository.PaisRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/paises")
@Tag(name = "Endereço", description = "Operações relacionadas a Países, Estados, Cidades e Endereços")
public class PaisController {

    @Autowired
    private PaisRepository repository;

    @GetMapping
    @Operation(
            summary = "Listar todos os países",
            description = "Retorna todos os países cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de países retornada com sucesso")
            }
    )
    @Cacheable("paises")
    public List<Pais> index() {
        return repository.findAll();
    }
}

