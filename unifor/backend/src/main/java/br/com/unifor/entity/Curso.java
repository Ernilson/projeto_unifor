package br.com.unifor.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="curso")
public class Curso extends PanacheEntity {
    @NotBlank
    public String nome;
    @NotBlank @Column(unique = true) public String codigo;
    public boolean ativo = true;

    public Curso() {
    }

    public Curso(String nome, String codigo, boolean ativo) {
        this.nome = nome;
        this.codigo = codigo;
        this.ativo = ativo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}

