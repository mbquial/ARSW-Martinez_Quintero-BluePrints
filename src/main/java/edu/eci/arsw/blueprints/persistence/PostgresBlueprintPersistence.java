package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Primary
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    @Autowired
    private BlueprintRepository repo;

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (repo.findByAuthorAndName(bp.getAuthor(), bp.getName()).isPresent()) {
            throw new BlueprintPersistenceException("Ya existe un blueprint con ese autor y nombre: " + bp.getAuthor() + "-" + bp.getName());
        }
        repo.save(bp);
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return repo.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException("No existe el blueprint: " + author + "-" + name));
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        List<Blueprint> results = repo.findByAuthor(author);
        if (results.isEmpty()) {
            throw new BlueprintNotFoundException("No existen blueprints para el autor: " + author);
        }
        return new HashSet<>(results);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(repo.findAll());
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Blueprint bp = getBlueprint(author, name);
        bp.addPoint(new Point(x, y));
        repo.save(bp);
    }
}