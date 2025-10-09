package br.com.unifor.service;

import br.com.unifor.entity.Curso;
import br.com.unifor.entity.MatrizCurricular;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class CursoService {

    public List<Curso> listarTodos() {
        return Curso.listAll();
    }

    public Curso buscarPorId(Long id) {
        return Curso.findByIdOptional(id)
                .map(c -> (Curso) c)
                .orElseThrow(() -> new NotFoundException("Curso com ID " + id + " não encontrado."));
    }

    @Transactional
    public Curso criar(Curso novoCurso) {
        // A lógica de negócio, como validações, pode ser adicionada aqui.
        novoCurso.id = null; // Garante que é uma nova inserção
        novoCurso.persist();
        return novoCurso;
    }

    @Transactional
    public Curso atualizar(Long id, Curso cursoData) {
        Curso curso = Curso.<Curso>findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Curso com ID " + id + " não encontrado."));

        // Atualiza os campos do objeto encontrado com os dados recebidos
        curso.nome = cursoData.nome;
        curso.codigo = cursoData.codigo;
        curso.ativo = cursoData.ativo;
        // O Panache gerencia a atualização, não é preciso chamar 'persist()'

        return curso;
    }

    @Transactional
    public void delete(Long id) {
        Curso curso = Curso.findById(id);
        if (curso == null) {
            throw new WebApplicationException("Curso não encontrado.", Response.Status.NOT_FOUND);
        }

        long countVinculos = MatrizCurricular.count("curso.id", id);
        if (countVinculos > 0) {
            throw new WebApplicationException(
                    "Não é possível excluir o curso. Existe uma matriz curricular vinculada a ele.",
                    Response.Status.CONFLICT
            );
        }

        curso.delete();
    }
}