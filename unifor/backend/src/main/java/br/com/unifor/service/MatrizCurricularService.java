package br.com.unifor.service;

import br.com.unifor.entity.Curso;
import br.com.unifor.entity.MatrizCurricular;
import br.com.unifor.entity.Semestre;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatrizCurricularService {

    public List<MatrizCurricular> list(Integer page, Integer size, Long cursoId, Long semestreId, Boolean ativa) {
        StringBuilder jpql = new StringBuilder("1=1");
        new Object() { int i = 0; };

        if (cursoId != null) jpql.append(" and curso.id = ").append(cursoId);
        if (semestreId != null) jpql.append(" and semestre.id = ").append(semestreId);
        if (ativa != null) jpql.append(" and ativa = ").append(ativa);

        PanacheQuery<MatrizCurricular> q = MatrizCurricular.find(jpql.toString());
        if (page != null && size != null) q = q.page(page, size);
        return q.list();
    }

    public Optional<MatrizCurricular> findById(Long id) {
        return MatrizCurricular.findByIdOptional(id);
    }

    public Optional<MatrizCurricular> findByCursoSemestre(Long cursoId, Long semestreId) {
        return MatrizCurricular.find("curso.id = ?1 and semestre.id = ?2", cursoId, semestreId).firstResultOptional();
    }

    @Transactional
    public MatrizCurricular create(Long cursoId, Long semestreId, Boolean ativa) {
        Optional<MatrizCurricular> existente = findByCursoSemestre(cursoId, semestreId);
        if (existente.isPresent()) {
            if (ativa != null) {
                existente.get().ativa = ativa;
            }
            return existente.get();
        }
        Curso curso = Curso.findById(cursoId);
        Semestre sem = Semestre.findById(semestreId);
        if (curso == null || sem == null) throw new NotFoundException("Curso ou Semestre não encontrado");

        MatrizCurricular m = new MatrizCurricular();
        m.curso = curso;
        m.semestre = sem;
        m.ativa = (ativa != null ? ativa : true);
        m.persist();
        return m;
    }

    @Transactional
    public MatrizCurricular update(Long id, Long novoCursoId, Long novoSemestreId, Boolean ativa) {
        MatrizCurricular m = (MatrizCurricular) MatrizCurricular.findByIdOptional(id).orElseThrow(NotFoundException::new);
        if (novoCursoId != null && (m.curso == null || !m.curso.id.equals(novoCursoId))) {
            Curso c = Curso.findById(novoCursoId);
            if (c == null) throw new NotFoundException("Curso não encontrado");
            m.curso = c;
        }
        if (novoSemestreId != null && (m.semestre == null || !m.semestre.id.equals(novoSemestreId))) {
            Semestre s = Semestre.findById(novoSemestreId);
            if (s == null) throw new NotFoundException("Semestre não encontrado");
            m.semestre = s;
        }
        if (ativa != null) m.ativa = ativa;
        return m;
    }

    @Transactional
    public void setAtiva(Long id, boolean value) {
        MatrizCurricular m = (MatrizCurricular) MatrizCurricular.findByIdOptional(id).orElseThrow(NotFoundException::new);
        m.ativa = value;
    }

    @Transactional
    public void delete(Long id) {
        boolean ok = MatrizCurricular.deleteById(id);
        if (!ok) throw new NotFoundException();
    }
}
