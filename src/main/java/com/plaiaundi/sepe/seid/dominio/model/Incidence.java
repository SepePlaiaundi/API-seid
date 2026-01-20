package com.plaiaundi.sepe.seid.dominio.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="incidencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incidence {

    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "id")
    private Recurso recurso;
    private String provincia;
    private String causa;
    private LocalDateTime fecIni;
    private String carretera;
    private String direccion;
    private String latitud;
    private String longitud;
    private String ciudad;
    private LocalDateTime fecFin;
    private String nivel;
    private String nombre;
    private String tipo;
    private String descripcion;

}
