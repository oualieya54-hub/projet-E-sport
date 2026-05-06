package org.example.controller;

import org.example.Model.Certification;
import org.example.Service.CertificationService;

import java.sql.SQLException;
import java.util.List;

public class CertificationController {
    private final CertificationService certificationService;

    public CertificationController() {
        this.certificationService = new CertificationService();
    }

    public void create(Certification c) throws SQLException {
        certificationService.create(c);
    }

    public List<Certification> getAll() throws SQLException {
        return certificationService.getAll();
    }

    public List<Certification> getByEleve(int idEleve) throws SQLException {
        return certificationService.getByEleve(idEleve);
    }

    public List<Certification> getByFormation(int idFormation) throws SQLException {
        return certificationService.getByFormation(idFormation);
    }

    public void update(Certification c) throws SQLException {
        certificationService.update(c);
    }

    public void delete(int idCertification) throws SQLException {
        certificationService.delete(idCertification);
    }

    public boolean hasAlreadyCertified(int idEleve, int idFormation) throws SQLException {
        return certificationService.hasAlreadyCertified(idEleve, idFormation);
    }

    public void createSafe(Certification c) throws SQLException {
        certificationService.createSafe(c);
    }

    public String calculerNiveau(float score) {
        return certificationService.calculerNiveau(score);
    }

    public List<Certification> getByNiveau(String niveau) throws SQLException {
        return certificationService.getByNiveau(niveau);
    }
}
