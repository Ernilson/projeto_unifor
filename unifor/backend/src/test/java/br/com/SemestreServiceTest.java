package br.com;

import br.com.unifor.entity.Semestre;
import br.com.unifor.service.SemestreService;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SemestreServiceTest {

    @InjectMocks
    SemestreService semestreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_quandoInputNulo_deveLancarBadRequestException() {
        // Cenário: Tenta criar com um objeto nulo.
        assertThrows(BadRequestException.class, () -> semestreService.create(null));
    }

    @Test
    void update_quandoSemestreExiste_deveAtualizarComSucesso() {

        Semestre semestreExistente = new Semestre();
        semestreExistente.id = 1L;
        semestreExistente.ano = 2024;
        semestreExistente.periodo = 2;
        semestreExistente.ativo = true;
        Semestre dadosParaAtualizar = new Semestre();
        dadosParaAtualizar.ano = 2025;
        dadosParaAtualizar.periodo = 1;
        dadosParaAtualizar.ativo = false;

    }

    @Test
    @TestTransaction
    void findById_quandoIdExiste_deveRetornarOptionalComSemestre() {
        Semestre semestre = new Semestre(2024, 2, true);
        semestre.persist();
        Long idExistente = semestre.id;

        Optional<Semestre> resultado = semestreService.findById(idExistente);

        assertTrue(resultado.isPresent());
        assertEquals(idExistente, resultado.get().id);
    }

    @Test
    @TestTransaction
    void findById_quandoIdNaoExiste_deveRetornarOptionalVazio() {
        Optional<Semestre> resultado = semestreService.findById(999L);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @TestTransaction
    void delete_quandoIdExisteESemVinculos_deveRemoverComSucesso() {
        Semestre semestre = new Semestre(2023, 1, true);
        semestre.persist();
        Long idExistente = semestre.id;

        assertDoesNotThrow(() -> semestreService.delete(idExistente));
        assertNull(Semestre.findById(idExistente));
    }

}