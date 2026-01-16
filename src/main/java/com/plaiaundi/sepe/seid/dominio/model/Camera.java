package com.plaiaundi.sepe.seid.dominio.model;

import java.net.URL;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="camaras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {
    
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
}
