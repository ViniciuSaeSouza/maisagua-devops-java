package br.com.fiap.mais_agua.specification;

import br.com.fiap.mais_agua.controller.LeituraDispositivoController.LeituraDispositivoFilter;
import br.com.fiap.mais_agua.model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class LeituraDispositivoSpecification {

    public static Specification<LeituraDispositivo> withFilters(LeituraDispositivoFilter filters, Usuario usuario) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            // Subquery para verificar se o dispositivo da leitura está vinculado a um reservatório do usuário
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<ReservatorioDispositivo> subRoot = subquery.from(ReservatorioDispositivo.class);
            Join<ReservatorioDispositivo, Reservatorio> subReservatorio = subRoot.join("reservatorio");

            subquery.select(subRoot.get("dispositivo").get("idDispositivo"))
                    .where(
                            cb.equal(subReservatorio.get("unidade").get("usuario"), usuario),
                            filters.idReservatorio() != null ?
                                    cb.equal(subReservatorio.get("idReservatorio"), filters.idReservatorio()) :
                                    cb.conjunction()
                    );

            // Filtra leituras que o dispositivo esteja na subquery
            predicates.add(root.get("dispositivo").get("idDispositivo").in(subquery));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
