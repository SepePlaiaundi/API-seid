package com.plaiaundi.sepe.seid.dominio.model;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="camaras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {
    public enum Estado {
        ACTIVA,
        ELIMINADA,
        SINCRONIZANDO,
    }

    @Id
    private int id;
    private String direccion;
    private String nombre;
    private int kilometro;
    private double latitud;
    private double longitud;
    private String carretera;
    private Recurso recurso;
    private URL urlImage;
    private LocalDateTime primeraInsercion = LocalDateTime.now();
    private LocalDateTime ultimaActualizacion;
    private boolean modificar = false;
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVA; // Activo o Eliminado

}
