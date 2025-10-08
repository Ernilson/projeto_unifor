package br.com;

import br.com.unifor.entity.Curso;
import br.com.unifor.service.CursoService;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CursoServiceTest {

    @Inject
    CursoService cursoService;

    @BeforeEach
    @TestTransaction
    void setup() {

        Curso.deleteAll();
    }

    private Curso criarCursoTeste(String nome, String codigo) {
        Curso curso = new Curso();
        curso.nome = nome;
        curso.codigo = codigo;
        curso.ativo = true;
        return cursoService.criar(curso);
    }

    @Test
    @TestTransaction
    void listarTodos_quandoNenhumCursoExiste_deveRetornarListaVazia() {
        List<Curso> cursos = cursoService.listarTodos();
        assertTrue(cursos.isEmpty());
    }

    @Test
    @TestTransaction
    void listarTodos_quandoCursosExistem_deveRetornarTodosCursos() {
        criarCursoTeste("Engenharia de Software", "ES001");
        criarCursoTeste("Direito", "DR002");

        List<Curso> cursos = cursoService.listarTodos();
        assertEquals(2, cursos.size());
        assertTrue(cursos.stream().anyMatch(c -> c.nome.equals("Direito")));
    }

    @Test
    @TestTransaction
    void criar_devePersistirNovoCurso() {
        Curso novoCurso = new Curso();
        novoCurso.nome = "Medicina";
        novoCurso.codigo = "ME003";
        novoCurso.ativo = true;

        Curso cursoCriado = cursoService.criar(novoCurso);

        assertNotNull(cursoCriado.id);
        assertEquals("Medicina", cursoCriado.nome);
        assertEquals(1, Curso.count()); // Verifica se foi realmente persistido
    }

    @Test
    @TestTransaction
    void atualizar_quandoCursoExiste_deveAtualizarDados() {

        Curso cursoExistente = criarCursoTeste("Biologia", "BI004");
        Long idExistente = cursoExistente.id;

        Curso dadosAtualizados = new Curso();
        dadosAtualizados.nome = "Biologia Marinha";
        dadosAtualizados.codigo = "BM004";
        dadosAtualizados.ativo = false;

        Curso cursoAtualizado = cursoService.atualizar(idExistente, dadosAtualizados);

        assertEquals(idExistente, cursoAtualizado.id);
        assertEquals("Biologia Marinha", cursoAtualizado.nome);
        assertFalse(cursoAtualizado.ativo);

        Curso cursoVerificado = Curso.findById(idExistente);
        assertEquals("Biologia Marinha", cursoVerificado.nome);
    }

    @Test
    @TestTransaction
    void atualizar_quandoCursoNaoExiste_deveLancarNotFoundException() {
        Long idInexistente = (Long) 999L;

        Curso dadosAtualizados = new Curso();
        dadosAtualizados.nome = "Inexistente";

        Assertions.assertThrows(NotFoundException.class, () -> {
            cursoService.atualizar(idInexistente, dadosAtualizados);
        });
    }

    @Test
    @TestTransaction
    void deletar_quandoCursoExiste_deveRetornarTrueEDeletar() {

        Curso cursoExistente = criarCursoTeste("Artes", "AR005");
        Long idExistente = cursoExistente.id;

        boolean sucesso = cursoService.deletar(idExistente);
        assertTrue(sucesso);

        assertEquals(0, Curso.count());
        assertTrue(Curso.findByIdOptional(idExistente).isEmpty());
    }

    @Test
    @TestTransaction
    void deletar_quandoCursoNaoExiste_deveRetornarFalse() {
        Long idInexistente = (Long) 888L;

        boolean sucesso = cursoService.deletar(idInexistente);
        assertFalse(sucesso);
        assertEquals(0, Curso.count()); // Garantir que não havia nada antes/depois
    }
}
