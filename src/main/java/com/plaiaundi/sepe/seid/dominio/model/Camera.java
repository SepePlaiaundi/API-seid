package com.plaiaundi.sepe.seid.dominio.model;

import java.net.URL;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@Table(name="camaras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera implements Persistable<Integer> {
    public enum Estado {
        ACTIVA,
        ELIMINADA,
        SINCRONIZANDO,
    }

    @Id
    private Integer id;
    private String direccion;
    private String nombre;
    private String kilometro;
    private double latitud;
    private double longitud;
    private String carretera;
    @ManyToOne
    @JoinColumn(name = "recurso_id")
    private Recurso recurso;
    private URL urlImage;
    private LocalDateTime primeraInsercion = LocalDateTime.now();
    private LocalDateTime ultimaActualizacion;
    private boolean modificar = false;
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVA; // Activo o Eliminado

    @Transient // Este campo no se guarda en BD, es solo para lógica
    private boolean isNew = true;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    // Método helper para usar en el Mapper
    public Camera markNotNew() {
        this.isNew = false;
        return this;
    }

}
