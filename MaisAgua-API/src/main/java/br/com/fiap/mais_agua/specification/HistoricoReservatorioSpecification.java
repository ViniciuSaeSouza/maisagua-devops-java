package br.com.fiap.mais_agua.specification;

import org.springframework.data.jpa.domain.Specification;
import br.com.fiap.mais_agua.model.HistoricoReservatorio;
import br.com.fiap.mais_agua.controller.HistoricoReservatorioController.HistoricoReservatorioFilters;
import br.com.fiap.mais_agua.model.Usuario;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;

public class HistoricoReservatorioSpecification {

    public static Specification<HistoricoReservatorio> withFilters(HistoricoReservatorioFilters filters, Usuario usuario) {
        return (root, query, cb) -> {

            var predicates = new ArrayList<Predicate>();

            predicates.add(
                    cb.equal(root.get("reservatorio").get("unidade").get("usuario"), usuario)
            );

            if (filters.idReservatorio() != null) {
                predicates.add(
                        cb.equal(root.get("reservatorio").get("idReservatorio"), filters.idReservatorio())  // Acessa o id do Reservatório
                );
            }

            // Filtro por nivelLitros
            if (filters.nivelLitros() != null) {
                predicates.add(
                        cb.equal(root.get("nivelLitros"), filters.nivelLitros())
                );
            }

            // Filtro por status
            if (filters.status() != null) {
                predicates.add(
                        cb.equal(root.get("status").get("id"), filters.status())  // Acessando o id de status
                );
            }

            var arrayPredicates = predicates.toArray(new Predicate[0]);
            query.where(cb.and(arrayPredicates));

            // Ordenação: do último id gerado até os primeiros
            query.orderBy(cb.desc(root.get("reservatorio").get("idReservatorio"))); // Ordena pelo id do Reservatório

            return query.getRestriction();
        };
    }
}
