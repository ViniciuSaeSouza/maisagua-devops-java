package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.DTO.CadastroCompletoDTO;
import br.com.fiap.mais_agua.model.DTO.CadastroCompletoResponseDTO;
import br.com.fiap.mais_agua.service.CadastroCompletoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuário", description = "Operações de cadastro e login do sistema +Água")
@RestController
@RequestMapping("/cadastro-completo")
@RequiredArgsConstructor
public class CadastroCompletoController {

    private final CadastroCompletoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cadastro completo do usuário",
            description = "Realiza o cadastro completo com dados do usuário, unidade e endereço",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Cidade não encontrada")
            }
    )
    public CadastroCompletoResponseDTO cadastrar(@RequestBody @Valid CadastroCompletoDTO dto) {
        return service.cadastrar(dto);
    }
}

