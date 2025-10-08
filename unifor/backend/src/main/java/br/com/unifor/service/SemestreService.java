package br.com.unifor.service;

import br.com.unifor.entity.Semestre;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SemestreService {

    public List<Semestre> list(int page, int size, Integer ano, Integer periodo, Boolean ativo) {
        StringBuilder jpql = new StringBuilder("1=1");
        if (ano != null)      jpql.append(" and ano = ").append(ano);
        if (periodo != null)  jpql.append(" and periodo = ").append(periodo);
        if (ativo != null)    jpql.append(" and ativo = ").append(ativo);

        PanacheQuery<Semestre> q = Semestre.find(jpql.toString()).page(page, size);
        return q.list();
    }

    public Optional<Semestre> findById(Long id) {
        return Semestre.findByIdOptional(id);
    }

    public Optional<Semestre> findByAnoPeriodo(int ano, int periodo) {
        return Semestre.find("ano = ?1 and periodo = ?2", ano, periodo).firstResultOptional();
    }

    @Transactional
    public Semestre create(Semestre in) {
        if (in == null) throw new BadRequestException("Body é obrigatório");
        // respeita a unique (ano, periodo)
        if (findByAnoPeriodo(in.ano, in.periodo).isPresent()) {
            throw new WebApplicationException("Semestre já existe para ano/periodo", Response.Status.CONFLICT);
        }
        in.id = null;
        in.persist();
        return in;
    }

    @Transactional
    public Semestre update(Long id, Semestre in) {
        if (in == null) throw new BadRequestException("Body é obrigatório");
        Semestre s = (Semestre) Semestre.findByIdOptional(id).orElseThrow(NotFoundException::new);

        // se mudar ano/periodo, validar unicidade
        boolean mudouAno = in.ano != 0 && in.ano != s.ano;
        boolean mudouPeriodo = in.periodo != 0 && in.periodo != s.periodo;
        if (mudouAno || mudouPeriodo) {
            Optional<Semestre> dup = findByAnoPeriodo(
                    mudouAno ? in.ano : s.ano,
                    mudouPeriodo ? in.periodo : s.periodo);
            if (dup.isPresent() && !dup.get().id.equals(s.id)) {
                throw new WebApplicationException("Já existe semestre com esse ano/período", Response.Status.CONFLICT);
            }
        }

        s.ano = in.ano;
        s.periodo = in.periodo;
        s.ativo = in.ativo;
        return s; // dirty-checking
    }

    @Transactional
    public void delete(Long id) {
        boolean ok = Semestre.deleteById(id);
        if (!ok) throw new NotFoundException();
    }
}
