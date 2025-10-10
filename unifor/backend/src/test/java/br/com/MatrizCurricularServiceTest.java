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

    @Inject
    TestSetupHelper testSetupHelper;

    // Limpa as tabelas antes de cada teste para garantir isolamento
    @BeforeEach
    @TestTransaction
    void setup() {
        MatrizCurricular.deleteAll();
        Curso.deleteAll();
        Semestre.deleteAll();
    }

    // region ====== Testes do método findById() e list() ======
    @Test
    @TestTransaction
    void findById_quandoMatrizExiste_deveRetornarOptionalComValor() {
        // Arrange
        Curso curso = testSetupHelper.criarCurso("Medicina", "MED01");
        Semestre semestre = testSetupHelper.criarSemestre(2024, 2);
        MatrizCurricular matriz = matrizService.create(curso.id, semestre.id, true);

        // Act
        Optional<MatrizCurricular> resultado = matrizService.findById(matriz.id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(matriz.id, resultado.get().id);
    }

    @Test
    @TestTransaction
    void setAtiva_deveAlterarOStatusDaMatriz() {
        // Arrange
        Curso curso = testSetupHelper.criarCurso("Psicologia", "PSI01");
        Semestre semestre = testSetupHelper.criarSemestre(2023, 1);
        MatrizCurricular matriz = matrizService.create(curso.id, semestre.id, true);
        assertTrue(matriz.ativa, "A matriz deve ser criada como ativa");

        // Act: Desativa a matriz
        matrizService.setAtiva(matriz.id, false);

        // Assert: Busca no banco e verifica o novo estado
        MatrizCurricular matrizAtualizada = MatrizCurricular.findById(matriz.id);
        assertNotNull(matrizAtualizada);
        assertFalse(matrizAtualizada.ativa, "A matriz deveria estar inativa");
    }


    // region ====== Testes do método delete() ======
    @Test
    @TestTransaction
    void delete_quandoMatrizExiste_deveRemoverComSucesso() {
        // Arrange
        Curso curso = testSetupHelper.criarCurso("Odontologia", "ODO01");
        Semestre semestre = testSetupHelper.criarSemestre(2022, 2);
        MatrizCurricular matriz = matrizService.create(curso.id, semestre.id, true);

        // Act
        assertDoesNotThrow(() -> matrizService.delete(matriz.id));

        // Assert
        assertNull(MatrizCurricular.findById(matriz.id), "A matriz não deveria mais ser encontrada no banco");
    }

    @Test
    @TestTransaction
    void create_quandoMatrizJaExiste_deveLancarException() {
        // Arrange
        Curso curso = testSetupHelper.criarCurso("Direito", "DIR01");
        Semestre semestre = testSetupHelper.criarSemestre(2024, 1);
        // Cria uma primeira matriz para simular a duplicidade
        matrizService.create(curso.id, semestre.id, true);

           assertThrows(IllegalStateException.class, () -> {
            matrizService.create(curso.id, semestre.id, true);
        });
    }

    @TestTransaction
    void update_quandoMatrizExiste_deveAlterarCursoEStatus() {
        // Arrange
        Curso cursoAntigo = testSetupHelper.criarCurso("Curso Antigo", "ANT01");
        Curso cursoNovo = testSetupHelper.criarCurso("Curso Novo", "NOV01");
        Semestre semestre = testSetupHelper.criarSemestre(2023, 2);
        MatrizCurricular matriz = matrizService.create(cursoAntigo.id, semestre.id, true);

        // Act: Atualiza a matriz para usar o cursoNovo e a desativa
        matrizService.update(matriz.id, cursoNovo.id, semestre.id, false);

        // Assert: Busca no banco para confirmar as alterações
        MatrizCurricular matrizAtualizada = MatrizCurricular.findById(matriz.id);
        assertNotNull(matrizAtualizada);
        assertEquals(cursoNovo.id, matrizAtualizada.curso.id);
        assertFalse(matrizAtualizada.ativa);
    }

    @Test
    @TestTransaction
    void delete_quandoMatrizNaoExiste_deveLancarNotFoundException() {
        // Arrange
        Long idInexistente = 999L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            matrizService.delete(idInexistente);
        });
    }


}