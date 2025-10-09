package br.com.unifor.entity.dto;

public record MatrizUpdateDTO(
        Long cursoId,
        Long semestreId,
        Boolean ativa
) {}
