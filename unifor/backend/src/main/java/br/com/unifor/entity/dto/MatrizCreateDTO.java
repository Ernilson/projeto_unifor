package br.com.unifor.entity.dto;

import jakarta.validation.constraints.NotNull;

public record MatrizCreateDTO(
        @NotNull Long cursoId,
        @NotNull Long semestreId,
        @NotNull Boolean ativa
) {}
