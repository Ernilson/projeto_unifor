package br.com;

import br.com.unifor.entity.Curso;
import br.com.unifor.entity.MatrizCurricular;
import br.com.unifor.entity.Semestre;
import br.com.unifor.service.MatrizCurricularService;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MatrizCurricularServiceIT {

    @Inject
    MatrizCurricularService matrizService;

    // Helper para criar um curso
    private Curso criarCurso(String nome, String codigo) {
        Curso curso = new Curso();
        curso.nome = nome;
        curso.codigo = codigo;
        curso.ativo = true;
        curso.persist();
        return curso;
    }

    // Helper para criar um semestre
    private Semestre criarSemestre(int ano, int periodo) {
        Semestre semestre = new Semestre();
        semestre.ano = ano;
        semestre.periodo = periodo;
        semestre.ativo = true;
        semestre.persist();
        return semestre;
    }

    // Limpa as tabelas antes de cada teste para garantir isolamento
    @BeforeEach
    @TestTransaction
    void setup() {
        MatrizCurricular.deleteAll();
        Curso.deleteAll();
        Semestre.deleteAll();
    }

    // region ====== Testes do método create() ======
    @Test
    @TestTransaction
    void create_comDadosValidos_deveCriarMatrizComSucesso() {
        // Arrange
        Curso curso = criarCurso("Engenharia de Software", "ES01");
        Semestre semestre = criarSemestre(2025, 1);

        // Act
        MatrizCurricular novaMatriz = matrizService.create(curso.id, semestre.id, true);

        // Assert
        assertNotNull(novaMatriz);
        assertNotNull(novaMatriz.id);
        assertEquals(curso.id, novaMatriz.curso.id);
        assertEquals(semestre.id, novaMatriz.semestre.id);
        assertTrue(novaMatriz.ativa);
    }

    @Test
    @TestTransaction
    void create_comCursoInexistente_deveLancarNotFoundException() {
        // Arrange
        Semestre semestre = criarSemestre(2025, 1);
        Long cursoIdInexistente = 999L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            matrizService.create(cursoIdInexistente, semestre.id, true);
        });
    }

    @Test
    @TestTransaction
    void create_quandoMatrizJaExiste_deveLancarException() {
        // Arrange
        Curso curso = criarCurso("Direito", "DIR01");
        Semestre semestre = criarSemestre(2025, 1);
        // Cria uma matriz para simular a duplicidade
        matrizService.create(curso.id, semestre.id, true);

        // Act & Assert: Tenta criar a mesma matriz novamente
        assertThrows(IllegalStateException.class, () -> {
            matrizService.create(curso.id, semestre.id, true);
        });
    }
    // endregion

    // region ====== Testes do método findById() e list() ======
    @Test
    @TestTransaction
    void findById_quandoMatrizExiste_deveRetornarOptionalComValor() {
        // Arrange
        Curso curso = criarCurso("Medicina", "MED01");
        Semestre semestre = criarSemestre(2024, 2);
        MatrizCurricular matriz = matrizService.create(curso.id, semestre.id, true);

        // Act
        Optional<MatrizCurricular> resultado = matrizService.findById(matriz.id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(matriz.id, resultado.get().id);
    }

    @Test
    @TestTransaction
    void list_comFiltroDeCurso_deveRetornarApenasMatrizesDoCurso() {
        // Arrange
        Curso cursoA = criarCurso("Curso A", "CA01");
        Curso cursoB = criarCurso("Curso B", "CB01");
        Semestre semestre = criarSemestre(2025, 1);
        matrizService.create(cursoA.id, semestre.id, true); // Matriz 1 (do curso A)
        matrizService.create(cursoB.id, semestre.id, true); // Matriz 2 (do curso B)

        // Act: Lista apenas as matrizes do Curso A
        List<MatrizCurricular> resultado = matrizService.list(0, 10, cursoA.id, null, null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(cursoA.id, resultado.get(0).curso.id);
    }
    // endregion

    // region ====== Testes do método setAtiva() ======
    @Test
    @TestTransaction
    void setAtiva_deveAlterarOStatusDaMatriz() {
        // Arrange
        Curso curso = criarCurso("Psicologia", "PSI01");
        Semestre semestre = criarSemestre(2023, 1);
        MatrizCurricular matriz = matrizService.create(curso.id, semestre.id, true);
        assertTrue(matriz.ativa, "A matriz deve ser criada como ativa");

        // Act: Desativa a matriz
        matrizService.setAtiva(matriz.id, false);

        // Assert: Busca no banco e verifica o novo estado
        MatrizCurricular matrizAtualizada = MatrizCurricular.findById(matriz.id);
        assertNotNull(matrizAtualizada);
        assertFalse(matrizAtualizada.ativa, "A matriz deveria estar inativa");
    }
    // endregion

    // region ====== Testes do método delete() ======
    @Test
    @TestTransaction
    void delete_quandoMatrizExiste_deveRemoverComSucesso() {
        // Arrange
        Curso curso = criarCurso("Odontologia", "ODO01");
        Semestre semestre = criarSemestre(2022, 2);
        MatrizCurricular matriz = matrizService.create(curso.id, semestre.id, true);

        // Act
        assertDoesNotThrow(() -> matrizService.delete(matriz.id));

        // Assert
        assertNull(MatrizCurricular.findById(matriz.id), "A matriz não deveria mais ser encontrada no banco");
    }
    // endregion
}