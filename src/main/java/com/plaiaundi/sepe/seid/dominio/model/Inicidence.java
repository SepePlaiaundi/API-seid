package com.plaiaundi.sepe.seid.dominio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="incidencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inicidence {

    @Id
    private String id;

}
