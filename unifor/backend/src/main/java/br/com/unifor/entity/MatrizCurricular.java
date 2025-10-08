package br.com.unifor.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "matriz_curricular",
        uniqueConstraints = @UniqueConstraint(name = "uk_matriz_curso_semestre", columnNames = {"curso_id","semestre_id"})
)
public class MatrizCurricular extends PanacheEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matriz_curso"))
    public Curso curso;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matriz_semestre"))
    public Semestre semestre;

    @Column(nullable = false)
    public boolean ativa = true;

    public MatrizCurricular() {
    }

    public MatrizCurricular(Curso curso, Semestre semestre, boolean ativa) {
        this.curso = curso;
        this.semestre = semestre;
        this.ativa = ativa;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}
