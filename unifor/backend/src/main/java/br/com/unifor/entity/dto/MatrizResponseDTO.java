package br.com.unifor.entity.dto;

public record MatrizResponseDTO(
        Long id,
        Long cursoId,
        String cursoNome,
        Long semestreId,
        String semestreLabel,
        Boolean ativa
) {}
