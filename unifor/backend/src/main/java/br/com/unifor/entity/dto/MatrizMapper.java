package br.com.unifor.entity.dto;

import br.com.unifor.entity.MatrizCurricular;

public final class MatrizMapper {

    private MatrizMapper(){}

    public static MatrizResponseDTO toDTO(MatrizCurricular m) {
        if (m == null) return null;
        var cursoNome = (m.curso != null ? m.curso.nome : null);
        var semestreLabel = (m.semestre != null ? (m.semestre.ano + " - " + m.semestre.periodo) : null);
        return new MatrizResponseDTO(
                m.id,
                (m.curso != null ? m.curso.id : null),
                cursoNome,
                (m.semestre != null ? m.semestre.id : null),
                semestreLabel,
                m.ativa
        );
    }
}

