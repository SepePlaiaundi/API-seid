package com.plaiaundi.sepe.seid.dominio.model;

import java.net.URL;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Entity
@Table(name = "camaras", uniqueConstraints = {
    @UniqueConstraint(name = "uk_camara_id_resource", columnNames = {"id", "recurso_id"})
})
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_model")
    @JsonIgnore
    private Integer idModel;

    @Column(name = "id")
    private Integer id;

    private String direccion;
    private String nombre;
    private String kilometro;
    private double latitud;
    private double longitud;
    private String carretera;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "recurso_id")
    private Recurso recurso;

    private URL urlImage;
    private LocalDateTime primeraInsercion = LocalDateTime.now();
    private LocalDateTime ultimaActualizacion;

    @JsonIgnore
    private boolean modificar = false;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVA; // Activo o Eliminado

    @Transient // Este campo no se guarda en BD, es solo para lógica
    @JsonProperty(value = "new", access = JsonProperty.Access.WRITE_ONLY)
    private boolean isNew = true;



}
