package edu.eci.arsw.blueprints.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Punto en coordenadas (x, y)")
public record Point(
        @Schema(description = "Coordenada X", example = "10") int x,
        @Schema(description = "Coordenada Y", example = "20") int y
) { }
