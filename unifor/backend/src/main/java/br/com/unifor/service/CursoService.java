package br.com.unifor.service;

import br.com.unifor.entity.Curso;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class CursoService {

    public List<Curso> listarTodos() {
        return Curso.listAll();
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
    public boolean deletar(Long id) {
        return Curso.deleteById(id);
    }
}