package com.plaiaundi.sepe.seid.dominio.model;

import java.net.URL;
import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name="camaras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {
    public class Estado {
        public static final String ACTIVA = "Activo";
        public static final String ELIMINADA = "Eliminado";
        public static final String SINCRONIZANDO = "Sincronizando";
    }

    @Id
    private String id;
    private String direccion;
    private String nombre;
    private String kilometro;
    private String latitud;
    private String longitud;
    private String carretera;
    //private Recurso recurso; // TODO: crear modelo recuros
    private URL urlImage;
    private LocalDateTime primeraInsercion = LocalDateTime.now();
    private LocalDateTime ultimaActualizacion;
    private boolean modificar = false;

    @Enumerated(EnumType.STRING)
    private String estado = Estado.ACTIVA; // Activo o Eliminado

}
