package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.HistoricoReservatorioRepository;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.UnidadeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    @Autowired
    private UnidadeRepository unidadeRepository;
    @Autowired
    private ReservatorioRepository reservatorioRepository;
    @Autowired
    private ReservatorioService reservatorioService;
    @Autowired
    HistoricoReservatorioRepository historicoReservatorioRepository;

    @Transactional
    public void deletarUnidade(Integer id, Usuario usuario) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        if (!unidade.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar esta unidade");
        }

        List<Reservatorio> reservatorios = reservatorioRepository.findByUnidade(unidade);

        for (Reservatorio r : reservatorios) {
            boolean existeHistorico = historicoReservatorioRepository.existsByReservatorio(r);
            if (existeHistorico) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Não é possível apagar a unidade porque o reservatório '" + r.getNome() + "' possui histórico vinculado.");
            }
        }

        for (Reservatorio reservatorio : reservatorios) {
            reservatorioService.deletarReservatorio(reservatorio.getIdReservatorio(), usuario);
        }

        unidadeRepository.delete(unidade);
    }

}

