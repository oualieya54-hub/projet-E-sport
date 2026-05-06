package org.example.controller;

import org.example.Model.Formation;
import org.example.Service.FormationService;

import java.sql.SQLException;
import java.util.List;

public class FormationController {
    private final FormationService formationService;

    public FormationController() {
        this.formationService = new FormationService();
    }

    public void create(Formation f) throws SQLException {
        formationService.create(f);
    }

    public List<Formation> getAll() throws SQLException {
        return formationService.getAll();
    }

    public Formation getById(int id) throws SQLException {
        return formationService.getById(id);
    }

    public void update(Formation f) throws SQLException {
        formationService.update(f);
    }

    public void delete(int idFormation) throws SQLException {
        formationService.delete(idFormation);
    }

    public List<Formation> getActive() throws SQLException {
        return formationService.getActive();
    }

    public List<Formation> getByJeu(String jeu) throws SQLException {
        return formationService.getByJeu(jeu);
    }

    public List<Formation> getByCoach(int idCoach) throws SQLException {
        return formationService.getByCoach(idCoach);
    }

    public void activer(int idFormation) throws SQLException {
        formationService.activer(idFormation);
    }

    public void archiver(int idFormation) throws SQLException {
        formationService.archiver(idFormation);
    }
}
