package br.com;

import br.com.unifor.entity.Disciplina;
import br.com.unifor.service.DisciplinaService;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DisciplinaServiceTest {

    @Inject
    DisciplinaService disciplinaService;


    @BeforeEach
    @TestTransaction
    void setup() {

        limparBanco();

        criarDisciplinaTeste("Estruturas de Dados", "ED001", 60);
        criarDisciplinaTeste("Algoritmos", "AL002", 60);
        criarDisciplinaTeste("Matemática Discreta", "MD003", 80);
        criarDisciplinaTeste("Arquitetura de Computadores", "AR004", 40);
    }

    private Disciplina criarDisciplinaTeste(String nome, String codigo, int cargaHoraria) {
        Disciplina d = new Disciplina();
        d.nome = nome;
        d.codigo = codigo;
        d.cargaHoraria = cargaHoraria;

        d.persist();
        return d;
    }

    @Transactional
    public void limparBanco() {
        Disciplina.deleteAll();
    }



    @Test
    @TestTransaction
    void list_comFiltro_retornaVazio() {
        List<Disciplina> resultado = disciplinaService.list(0, 10, "fisica");
        assertTrue(resultado.isEmpty());
    }

    @Test
    @TestTransaction
    void findById_quandoEncontrado_deveRetornarOptionalComDisciplina() {
        Disciplina d = criarDisciplinaTeste("Cálculo", "CA005", 100);
        Optional<Disciplina> resultado = disciplinaService.findById(d.id);

        assertTrue(resultado.isPresent());
        assertEquals("Cálculo", resultado.get().nome);
    }

    @Test
    @TestTransaction
    void findById_quandoNaoEncontrado_deveRetornarOptionalVazio() {
        Optional<Disciplina> resultado = disciplinaService.findById(Long.valueOf(999L));
        assertTrue(resultado.isEmpty());
    }


    @Test
    @TestTransaction
    void findByCodigo_quandoNaoEncontrado_deveRetornarOptionalVazio() {
        Optional<Disciplina> resultado = disciplinaService.findByCodigo("XYZ999");
        assertTrue(resultado.isEmpty());
    }

    @Test
    @TestTransaction
    void create_devePersistirNovaDisciplina() {
        Disciplina nova = new Disciplina();
        nova.nome = "Redes de Computadores";
        nova.codigo = "RC006";
        nova.cargaHoraria = 80;

        // O setup tem 4 disciplinas, o count deve ser 5 após a criação
        long countAntes = Disciplina.count();
        Disciplina criada = disciplinaService.create(nova);

        assertNotNull(criada.id);
        assertEquals(countAntes + 1, Disciplina.count());
    }

    @Test
    @TestTransaction
    void update_quandoEncontrado_deveAtualizarDados() {
        Disciplina d = criarDisciplinaTeste("Economia", "EC007", 60);
        Long idExistente = d.id;

        Disciplina dadosAtualizados = new Disciplina();
        dadosAtualizados.nome = "Microeconomia";
        dadosAtualizados.codigo = "MC007";
        dadosAtualizados.cargaHoraria = 40;

        Disciplina atualizada = disciplinaService.update(idExistente, dadosAtualizados);

        assertEquals("Microeconomia", atualizada.nome);
        assertEquals("MC007", atualizada.codigo);
        assertEquals(40, atualizada.cargaHoraria);

        Disciplina verificada = Disciplina.findById(idExistente);
        assertEquals("Microeconomia", verificada.nome);
    }

    @Test
    @TestTransaction
    void update_quandoNaoEncontrado_deveLancarNotFoundException() {
        Disciplina dados = new Disciplina();
        dados.nome = "Inexistente";

        Assertions.assertThrows(NotFoundException.class, () -> {
            disciplinaService.update(Long.valueOf(999L), dados);
        });
    }

    @Test
    @TestTransaction
    void delete_quandoEncontrado_deveDeletar() {
        Disciplina d = criarDisciplinaTeste("Estudos Avancados", "EA008", 20);
        Long idExistente = d.id;

        long countAntes = Disciplina.count();

        disciplinaService.delete(idExistente);

        assertEquals(countAntes - 1, Disciplina.count());

        assertTrue(Disciplina.findByIdOptional(idExistente).isEmpty());
    }

    @Test
    @TestTransaction
    void delete_quandoNaoEncontrado_deveLancarNotFoundException() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            disciplinaService.delete(Long.valueOf(999L));
        });
    }
}