package br.com;

import br.com.unifor.entity.Curso;
import br.com.unifor.entity.Disciplina;
import br.com.unifor.entity.Semestre;
import br.com.unifor.entity.MatrizCurricular;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TestSetupHelper {

    @Transactional
    public void limparBanco() {
        MatrizCurricular.deleteAll();
        Semestre.deleteAll();
        Curso.deleteAll();
    }

    // Cria e persiste a entidade Curso com COMMIT.
    @Transactional
    public Curso criarCurso(String nome) {
        Curso c = new Curso();
        c.nome = nome;
        c.ativo = true;
        c.codigo = "1";
        c.persist();
        return c;
    }

    // Cria e persiste a entidade Semestre com COMMIT.
    @Transactional
    public Semestre criarSemestre(String descricao) {
        Semestre s = new Semestre();
        s.periodo  = 1;
        s.ativo = true;
        s.ano = 2025;
        s.persist();
        return s;
    }

    @Transactional
    public Semestre criarSemestre(int i, int i1, boolean b) {
        Semestre s = new Semestre();
        s.periodo = i;
        s.ativo = b;
        s.ano = 2025;
        s.persist();
        return s;
    }

    public MatrizCurricular criarMatriz(Curso cursoEng, Semestre sem1, boolean b) {
        MatrizCurricular m = new MatrizCurricular();
        m.curso = cursoEng;
        m.semestre = sem1;
        m.ativa = true;
        m.persist();
        return m;
    }

    public Disciplina criarDisciplina(String cálculoI, String c001, int i) {
        Disciplina d = new Disciplina();
        d.nome = "teste1";
        d.codigo = "1";
        d.cargaHoraria = 40;
        return d;
    }
}
