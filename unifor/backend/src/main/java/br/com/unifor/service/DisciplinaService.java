package br.com.unifor.service;

import br.com.unifor.entity.Disciplina;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DisciplinaService {

    public List<Disciplina> list(int page, int size, String q) {
        PanacheQuery<Disciplina> query;
        if (q != null && !q.isBlank()) {
            // busca por nome ou código (case-insensitive)
            query = Disciplina.find("lower(nome) like ?1 or lower(codigo) like ?1",
                    "%" + q.toLowerCase().trim() + "%");
        } else {
            query = Disciplina.findAll();
        }
        return query.page(page, size).list();
    }

    public Optional<Disciplina> findById(Long id) {
        return Disciplina.findByIdOptional(id);
    }

    public Optional<Disciplina> findByCodigo(String codigo) {
        return Disciplina.find("codigo = ?1", codigo).firstResultOptional();
    }

    @Transactional
    public Disciplina create(Disciplina in) {
        in.id = null;
        in.persist();
        return in;
    }

    @Transactional
    public Disciplina update(Long id, Disciplina in) {
        Disciplina d = (Disciplina) Disciplina.findByIdOptional(id).orElseThrow(NotFoundException::new);
        d.codigo = in.codigo;
        d.nome = in.nome;
        d.cargaHoraria = in.cargaHoraria;
        // Panache faz dirty checking; não precisa chamar merge()
        return d;
    }

    @Transactional
    public void delete(Long id) {
        boolean ok = Disciplina.deleteById(id);
        if (!ok) throw new NotFoundException();
    }
}
