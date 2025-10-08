package br.com;

import br.com.unifor.entity.Semestre;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SemestreTest {

    @Inject
    Validator validator;

    @Inject
    TestSetupHelper helper;

    @Test
    @TestTransaction
    void persist_deveCriarSemestreValido() {
        Semestre semestre = new Semestre(2025, 1, true);
        semestre.persist();

        assertNotNull(semestre.id);
        assertEquals(2025, semestre.ano);
        assertEquals(1, semestre.periodo);
        assertEquals(1, Semestre.count());
    }

    @Test
    @TestTransaction
    void delete_deveRemoverSemestreComSucesso() {
        helper.limparBanco(); // Garante o estado inicial (0)
        Semestre semestre = new Semestre(2025, 2, true);
        semestre.persist();

        boolean deletado = Semestre.deleteById(semestre.id);

        assertTrue(deletado);
        assertEquals(0, Semestre.count());
    }

    @Test
    @TestTransaction
    void persist_devePermitirPeriodosDiferentesNoMesmoAno() {

        Semestre s1 = new Semestre(2027, 1, true);
        s1.persist();

        Semestre s2 = new Semestre(2027, 2, true);
        s2.persist();

        assertEquals(2, Semestre.count());
    }

    @Test
    void validate_anoAbaixoDoMinimo_deveFalhar() {
        Semestre semestreInvalido = new Semestre(1999, 1, true);

        Set<ConstraintViolation<Semestre>> violations = validator.validate(semestreInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ano")));
    }

    @Test
    void validate_periodoInvalido_deveFalhar() {
        Semestre semestreInvalido = new Semestre(2025, 3, true);

        Set<ConstraintViolation<Semestre>> violations = validator.validate(semestreInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("periodo")));
    }

    @Test
    @TestTransaction
    void persist_deveFalharQuandoAnoNaoDefinido() {
        Semestre semestre = new Semestre();
        semestre.periodo = 1;
        semestre.ano = 0;
        Set<ConstraintViolation<Semestre>> violations = validator.validate(semestre);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ano")));
    }
}