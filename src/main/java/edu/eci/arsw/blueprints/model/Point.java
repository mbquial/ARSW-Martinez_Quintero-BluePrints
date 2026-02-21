package edu.eci.arsw.blueprints.model;

import jakarta.persistence.Embeddable;
import io.swagger.v3.oas.annotations.media.Schema;

@Embeddable
@Schema(description = "Punto en coordenadas (x, y)")
public class Point {
    private int x;
    private int y;

    public Point() {}

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
