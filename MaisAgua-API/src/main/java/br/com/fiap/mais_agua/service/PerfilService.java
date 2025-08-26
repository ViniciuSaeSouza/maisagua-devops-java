package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.DTO.PerfilDTO;
import br.com.fiap.mais_agua.model.Endereco;
import br.com.fiap.mais_agua.model.HistoricoReservatorio;
import br.com.fiap.mais_agua.model.LeituraDispositivo;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PerfilService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ReservatorioDispositivoRepository reservatorioDispositivoRepository;

    @Autowired
    private HistoricoReservatorioRepository historicoReservatorioRepository;

    @Autowired
    private LeituraDispositivoRepository leituraDispositivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public PerfilDTO getPerfil(Integer idReservatorio, Usuario usuario) {
        // 1. Buscar o ReservatórioDispositivo associado ao idReservatorio
        ReservatorioDispositivo reservatorioDispositivo = reservatorioDispositivoRepository
                .findByReservatorioIdReservatorio(idReservatorio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reservatório não encontrado"));

        // 2. Verificar se o reservatório pertence ao usuário autenticado
        if (!reservatorioDispositivo.getReservatorio().getUnidade().getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este reservatório");
        }

        // 3. Buscar o endereço do usuário
        List<Endereco> enderecos = enderecoRepository.findByUnidadeUsuario(usuario);
        Endereco endereco = enderecos.stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Endereço não encontrado"));

        Reservatorio reservatorio = reservatorioDispositivo.getReservatorio();
        Dispositivo dispositivo = reservatorioDispositivo.getDispositivo();

        // 4. Buscar o último Histórico de Reservatório
        HistoricoReservatorio historicoReservatorio = historicoReservatorioRepository
                .findTopByReservatorioIdReservatorioOrderByDataHoraDesc(reservatorio.getIdReservatorio())
                .orElse(null);

        // 5. Buscar a última Leitura de Dispositivo
        LeituraDispositivo leituraDispositivo = leituraDispositivoRepository
                .findTopByDispositivoIdDispositivoOrderByDataHoraDesc(dispositivo.getIdDispositivo())
                .orElse(null);

        // 6. Buscar o usuário completo
        Optional<Usuario> usuarioRepoOptional = usuarioRepository.findByIdUsuario(usuario.getIdUsuario());

        if (usuarioRepoOptional.isPresent()) {
            Usuario usuarioRepo = usuarioRepoOptional.get();

            // 7. Montar e retornar o PerfilDTO com os dados do perfil do usuário, histórico e leitura do dispositivo
            return PerfilDTO.builder()
                    .nome(usuarioRepo.getNome())
                    .logradouro(endereco.getLogradouro())
                    .numero(endereco.getNumero())
                    .nivelLitros(historicoReservatorio != null ? historicoReservatorio.getNivelLitros() : 0)
                    .ph(leituraDispositivo != null ? leituraDispositivo.getPhInt() : BigDecimal.ZERO)
                    .nivelPct(leituraDispositivo != null ? leituraDispositivo.getNivelPct() : 0)
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com o id: " + usuario.getIdUsuario());
        }
    }
}
