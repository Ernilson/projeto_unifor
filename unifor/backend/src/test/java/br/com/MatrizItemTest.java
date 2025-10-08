package br.com;

import br.com.TestSetupHelper;
import br.com.unifor.entity.*;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MatrizItemTest {

    @Inject
    Validator validator;

    @Inject
    TestSetupHelper helper;

    // Dependências que serão criadas no setup
    private MatrizCurricular matrizEng;
    private Disciplina disciplinaCalc1;
    private Disciplina disciplinaCalc2;

    @BeforeEach
    void setup() {
        helper.limparBanco();
        Curso cursoEng = helper.criarCurso("Engenharia de Software");
        Semestre sem1 = helper.criarSemestre(2025, 1, true);

        matrizEng = helper.criarMatriz(cursoEng, sem1, true);

        disciplinaCalc1 = helper.criarDisciplina("Cálculo I", "C001", 80);
        disciplinaCalc2 = helper.criarDisciplina("Cálculo II", "C002", 80);
    }


    @Test
    @TestTransaction
    void persist_deveCriarMatrizItemValido() {
        MatrizItem item = new MatrizItem(matrizEng, disciplinaCalc1, 1);
        item.persist();

        assertNotNull(item.id);
        assertEquals(matrizEng.id, item.matriz.id);
        assertEquals(disciplinaCalc1.id, item.disciplina.id);
        assertEquals(1, item.ordem);
        assertEquals(1, MatrizItem.count());
    }

    @Test
    @TestTransaction
    void findById_deveBuscarItemCorretamente() {
        MatrizItem original = new MatrizItem(matrizEng, disciplinaCalc2, 2);
        original.persist();

        MatrizItem encontrado = MatrizItem.findById(original.id);

        assertNotNull(encontrado);
        assertEquals(disciplinaCalc2.id, encontrado.disciplina.id);
    }

    @Test
    @TestTransaction
    void persist_deveFalharAoCriarCombinacaoMatrizDisciplinaDuplicada() {
        MatrizItem item1 = new MatrizItem(matrizEng, disciplinaCalc1, 1);
        item1.persist();
        MatrizItem itemDuplicado = new MatrizItem(matrizEng, disciplinaCalc1, 5); // Ordem diferente não importa

        Assertions.assertThrows(PersistenceException.class, () -> {
            itemDuplicado.persistAndFlush(); // Força a verificação da restrição no banco
        }, "A restrição UNIQUE (matriz_id, disciplina_id) deve ser violada.");

        assertEquals(1, MatrizItem.count());
    }

    @Test
    @TestTransaction
    void persist_devePermitirMesmaDisciplinaEmOutraMatriz() {
        // 1. Cria a matriz Secundária (Direito)
        Curso cursoDir = helper.criarCurso("Direito");
        Semestre sem2 = helper.criarSemestre(2025, 2, true);
        MatrizCurricular matrizDir = helper.criarMatriz(cursoDir, sem2, true);

        // 2. Associa Cáculo I à Matriz de Engenharia
        MatrizItem itemEng = new MatrizItem(matrizEng, disciplinaCalc1, 1);
        itemEng.persist();

        // 3. Associa Cáculo I à Matriz de Direito (Permitido)
        MatrizItem itemDir = new MatrizItem(matrizDir, disciplinaCalc1, 1);
        itemDir.persist();

        assertEquals(2, MatrizItem.count());
    }

    @Test
    void validate_ordemMenorQueUm_deveFalhar() {
        // Ordem inválida: 0 (Min é 1)
        MatrizItem itemInvalido = new MatrizItem(matrizEng, disciplinaCalc1, 0);

        Set<ConstraintViolation<MatrizItem>> violations = validator.validate(itemInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ordem")));
    }

    @Test
    void validate_ordemValida_devePassar() {
        MatrizItem itemValido = new MatrizItem(matrizEng, disciplinaCalc1, 1);

        Set<ConstraintViolation<MatrizItem>> violations = validator.validate(itemValido);

        assertTrue(violations.isEmpty());
    }

    @Test
    @TestTransaction
    void persist_deveFalharQuandoMatrizForNula() {
        // Tenta persistir com Matriz = null
        MatrizItem item = new MatrizItem(null, disciplinaCalc1, 1);

        // Deve lançar a exceção de banco de dados (Constraint Violation)
        Assertions.assertThrows(PersistenceException.class, () -> {
            item.persistAndFlush();
        }, "Deve falhar por 'matriz_id' ser NOT NULL.");
    }

    @Test
    @TestTransaction
    void persist_deveFalharQuandoDisciplinaForNula() {
        // Tenta persistir com Disciplina = null
        MatrizItem item = new MatrizItem(matrizEng, null, 1);

        Assertions.assertThrows(PersistenceException.class, () -> {
            item.persistAndFlush();
        }, "Deve falhar por 'disciplina_id' ser NOT NULL.");
    }
}
