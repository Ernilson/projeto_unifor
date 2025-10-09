package br.com.unifor.service;

import br.com.unifor.entity.Curso;
import br.com.unifor.entity.MatrizCurricular;
import br.com.unifor.entity.Semestre;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatrizCurricularService {

    public List<MatrizCurricular> list(Integer page, Integer size, Long cursoId, Long semestreId, Boolean ativa) {

        String where = "1=1";
        Parameters params = new Parameters();

        if (cursoId != null) {
            where += " and curso.id = :cursoId";
            params = params.and("cursoId", cursoId);
        }
        if (semestreId != null) {
            where += " and semestre.id = :semestreId";
            params = params.and("semestreId", semestreId);
        }
        if (ativa != null) {
            where += " and ativa = :ativa";
            params = params.and("ativa", ativa);
        }

        PanacheQuery<MatrizCurricular> q = MatrizCurricular.find(where, params);

        if (page != null && size != null) {
            q = q.page(Page.of(page, size));
        }
        return q.list();
    }

    public Optional<MatrizCurricular> findById(Long id) {
        return MatrizCurricular.findByIdOptional(id);
    }

    public Optional<MatrizCurricular> findByCursoSemestre(Long cursoId, Long semestreId) {
        return MatrizCurricular.find("curso.id = ?1 and semestre.id = ?2", cursoId, semestreId)
                .firstResultOptional();
    }

    @Transactional
    public MatrizCurricular create(Long cursoId, Long semestreId, Boolean ativa) {
        // --- 1. Busca o Curso (usando find) ---
        Optional<Curso> optionalCurso = Curso.find("id", cursoId).firstResultOptional();
        if (!optionalCurso.isPresent()) {
            throw new NotFoundException("Curso com ID " + cursoId + " não encontrado.");
        }
        Curso curso = optionalCurso.get();

        // --- 2. Busca o Semestre (usando find) ---
        Optional<Semestre> optionalSemestre = Semestre.find("id", semestreId).firstResultOptional();
        if (!optionalSemestre.isPresent()) {
            // Você verá que o erro continuará acontecendo aqui.
            throw new NotFoundException("Semestre com ID " + semestreId + " não encontrado.");
        }
        Semestre semestre = optionalSemestre.get();

        // --- 3. Verifica duplicidade ---
        Optional<MatrizCurricular> optionalExistente = MatrizCurricular.find("curso = ?1 and semestre = ?2", curso, semestre)
                .firstResultOptional();
        if (optionalExistente.isPresent()) {
            throw new IllegalStateException("Matriz Curricular para este curso e semestre já existe.");
        }

        // --- 4. Cria e persiste a nova matriz ---
        boolean valorAtiva = (ativa != null) ? ativa : true;
        MatrizCurricular novaMatriz = new MatrizCurricular(curso, semestre, valorAtiva);
        novaMatriz.persist();
        return novaMatriz;
    }

    @Transactional
    public MatrizCurricular update(Long id, Long novoCursoId, Long novoSemestreId, Boolean ativa) {
        MatrizCurricular m = MatrizCurricular.<MatrizCurricular>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matriz Curricular com ID " + id + " não encontrada."));

        if (novoCursoId != null && (m.curso == null || !m.curso.id.equals(novoCursoId))) {
            Curso c = (Curso) Curso.findByIdOptional(novoCursoId)
                    .orElseThrow(() -> new NotFoundException("Curso com ID " + novoCursoId + " não encontrado."));
            m.curso = c;
        }

        if (novoSemestreId != null && (m.semestre == null || !m.semestre.id.equals(novoSemestreId))) {
            Semestre s = (Semestre) Semestre.findByIdOptional(novoSemestreId)
                    .orElseThrow(() -> new NotFoundException("Semestre com ID " + novoSemestreId + " não encontrado."));
            m.semestre = s;
        }

        if (ativa != null) m.ativa = ativa;

        return m;
    }

    @Transactional
    public void setAtiva(Long id, boolean value) {
        MatrizCurricular m = MatrizCurricular.<MatrizCurricular>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matriz Curricular com ID " + id + " não encontrada."));
        m.ativa = value;
    }

    @Transactional
    public void delete(Long id) {
        boolean ok = MatrizCurricular.deleteById(id);
        if (!ok) {
            throw new NotFoundException("Matriz Curricular com ID " + id + " não encontrada.");
        }
    }
}

