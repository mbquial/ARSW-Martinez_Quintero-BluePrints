package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/blueprints")
@Tag(name = "Blueprints", description = "API para gestión de planos (blueprints)")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    // GET /blueprints
    @Operation(summary = "Obtiene todos los blueprints")
    @ApiResponse(responseCode = "200", description = "Lista de todos los planos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<Set<Blueprint>> getAll() {
        return ResponseEntity.ok(services.getAllBlueprints());
    }

    // GET /blueprints/{author}
    @Operation(summary = "Obtiene los blueprints filtrados por autor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planos del autor encontrados"),
        @ApiResponse(responseCode = "404", description = "No se encontraron planos para el autor especificado")
    })
    @GetMapping("/{author}")
    public ResponseEntity<?> byAuthor(@PathVariable String author) {
        try {
            return ResponseEntity.ok(services.getBlueprintsByAuthor(author));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // GET /blueprints/{author}/{bpname}
    @Operation(summary = "Obtiene un blueprint específico por autor y nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plano encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el plano especificado")
    })
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<?> byAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            return ResponseEntity.ok(services.getBlueprint(author, bpname));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /blueprints
    @Operation(summary = "Crea un nuevo blueprint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plano creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "El plano ya existe o no se puede crear")
    })
    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /blueprints/{author}/{bpname}/points
    @Operation(summary = "Agrega un punto a un blueprint existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Punto agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el plano especificado")
    })
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<?> addPoint(@PathVariable String author, @PathVariable String bpname,
                                      @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}