package br.com.unifor.service;


import br.com.unifor.entity.DTO.MatrizDTO;
import br.com.unifor.entity.Curso;
import br.com.unifor.entity.MatrizCurricular;
import br.com.unifor.entity.Semestre;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class MatrizService {

    public List<MatrizDTO> listarTodos() {
        return MatrizCurricular.<MatrizCurricular>findAll()
                .list()
                .stream()
                .map(MatrizDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<MatrizDTO> buscarPorCursoESemestre(Long cursoId, Long semestreId) {
        Curso curso = Curso.findById(cursoId);
        Semestre semestre = Semestre.findById(semestreId);
        if (curso == null || semestre == null) {
            return Optional.empty();
        }

        PanacheQuery<MatrizCurricular> q = MatrizCurricular.find(
                "curso = ?1 and semestre = ?2", curso, semestre
        ).page(0, 1);

        return q.firstResultOptional().map(MatrizDTO::new);
    }

    @Transactional
    public Response criarOuObter(MatrizDTO dto) {
        if (dto == null || dto.getCursoId() == null || dto.getSemestreId() == null) {
            throw new WebApplicationException("cursoId e semestreId são obrigatórios", Response.Status.BAD_REQUEST);
        }

        Curso curso = Curso.findById(dto.getCursoId());
        Semestre semestre = Semestre.findById(dto.getSemestreId());

        if (curso == null) {
            throw new WebApplicationException("Curso não encontrado: id=" + dto.getCursoId(), Response.Status.NOT_FOUND);
        }
        if (semestre == null) {
            throw new WebApplicationException("Semestre não encontrado: id=" + dto.getSemestreId(), Response.Status.NOT_FOUND);
        }

        // Verifica se já existe
        Optional<MatrizCurricular> existente = MatrizCurricular.find(
                "curso = ?1 and semestre = ?2", curso, semestre
        ).firstResultOptional();

        if (existente.isPresent()) {
            // 200 OK devolvendo a existente
            return Response.ok(new MatrizDTO(existente.get())).build();
        }

        // Cria nova
        MatrizCurricular nova = dto.toEntity(curso, semestre);
        nova.persist();

        MatrizDTO resposta = new MatrizDTO(nova);
        return Response.created(URI.create("/api/matrizes/" + nova.id))
                .entity(resposta)
                .build();
    }
}
