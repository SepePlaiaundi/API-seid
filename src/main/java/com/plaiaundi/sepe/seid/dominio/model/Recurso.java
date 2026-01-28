package com.plaiaundi.sepe.seid.dominio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recursos")
public class Recurso {

    @Id
    private int id;
    private String descEs;
    private String descEu;

    public Recurso() {
    }

    public Recurso(int id, String descEs, String descEu) {
        this.id = id;
        this.descEs = descEs;
        this.descEu = descEu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescEs() {
        return descEs;
    }

    public void setDescEs(String descEs) {
        this.descEs = descEs;
    }

    public String getDescEu() {
        return descEu;
    }

    public void setDescEu(String descEu) {
        this.descEu = descEu;
    }
}
