package br.com.unifor.entity.DTO;

import br.com.unifor.entity.Curso;
import br.com.unifor.entity.Semestre;
import br.com.unifor.entity.MatrizCurricular;

/**
 * DTO para transferência de dados da entidade MatrizCurricular.
 *
 * Usado tanto para requisições (request) quanto respostas (response).
 */
public class MatrizDTO {

    private Long id;
    private Long cursoId;
    private Long semestreId;
    private Boolean ativa;

    private String nomeCurso;
    private String descricaoSemestre;

    public MatrizDTO() {
    }

    public MatrizDTO(MatrizCurricular entidade) {
        this.id = entidade.id;
        this.cursoId = entidade.curso != null ? entidade.curso.id : null;
        this.semestreId = entidade.semestre != null ? entidade.semestre.id : null;
        this.ativa = entidade.ativa;

        this.nomeCurso = entidade.curso != null ? entidade.curso.nome : null;
        this.descricaoSemestre = String.valueOf(entidade.semestre != null ? entidade.semestre.getPeriodo() : null);
    }

    public MatrizCurricular toEntity(Curso curso, Semestre semestre) {
        MatrizCurricular entidade = new MatrizCurricular();
        entidade.id = this.id;
        entidade.curso = curso;
        entidade.semestre = semestre;
        entidade.ativa = this.ativa != null ? this.ativa : false;
        return entidade;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public Long getSemestreId() { return semestreId; }
    public void setSemestreId(Long semestreId) { this.semestreId = semestreId; }

    public Boolean getAtiva() { return ativa; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }

    public String getNomeCurso() { return nomeCurso; }
    public void setNomeCurso(String nomeCurso) { this.nomeCurso = nomeCurso; }

    public String getDescricaoSemestre() { return descricaoSemestre; }
    public void setDescricaoSemestre(String descricaoSemestre) { this.descricaoSemestre = descricaoSemestre; }
}
