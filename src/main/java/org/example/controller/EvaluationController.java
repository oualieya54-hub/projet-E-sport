package org.example.controller;

import org.example.Model.Evaluation;
import org.example.Service.EvaluationService;

import java.sql.SQLException;
import java.util.List;

public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController() {
        this.evaluationService = new EvaluationService();
    }

    public void create(Evaluation e) throws SQLException {
        evaluationService.create(e);
    }

    public List<Evaluation> getAll() throws SQLException {
        return evaluationService.getAll();
    }

    public Evaluation getByBooking(int idBooking) throws SQLException {
        return evaluationService.getByBooking(idBooking);
    }

    public void update(Evaluation e) throws SQLException {
        evaluationService.update(e);
    }

    public void delete(int idEvaluation) throws SQLException {
        evaluationService.delete(idEvaluation);
    }

    public double getNoteMoyenneCoach(int idCoach) throws SQLException {
        return evaluationService.getNoteMoyenneCoach(idCoach);
    }

    public List<Evaluation> getByFormation(int idFormation) throws SQLException {
        return evaluationService.getByFormation(idFormation);
    }

    public boolean hasAlreadyEvaluated(int idBooking) throws SQLException {
        return evaluationService.hasAlreadyEvaluated(idBooking);
    }

    public void createSafe(Evaluation e) throws SQLException {
        evaluationService.createSafe(e);
    }
}
