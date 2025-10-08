package br.com;

import br.com.unifor.entity.Semestre;
import br.com.unifor.service.SemestreService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class SemestreServiceTest {

    @Inject
    SemestreService service;

    @Test
    void deveCriarUmSemestreComSucesso() {

        Semestre semestre = new Semestre();
        semestre.ano = 2027;
        semestre.periodo = 1;
        semestre.ativo = true;

        Semestre SemestreCriado = service.create(semestre);

        Assertions.assertNotNull(semestre, "O curso criado não deve ser nulo.");

        Assertions.assertNotNull(semestre.id, "O ID do curso deve ser gerado pelo BD.");

        Assertions.assertEquals(1, SemestreCriado.periodo);
    }
}
