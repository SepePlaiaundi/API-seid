package com.plaiaundi.sepe.seid.dominio.model;

import java.net.URL;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "camaras", uniqueConstraints = {
        @UniqueConstraint(name = "uk_camara_id_resource", columnNames = { "id", "recurso_id" })
})
@NoArgsConstructor
@AllArgsConstructor
public class Camera implements Persistable<Integer> {

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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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

    // Manual Getters and Setters
    public Integer getIdModel() {
        return idModel;
    }

    public void setIdModel(Integer idModel) {
        this.idModel = idModel;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getKilometro() {
        return kilometro;
    }

    public void setKilometro(String kilometro) {
        this.kilometro = kilometro;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getCarretera() {
        return carretera;
    }

    public void setCarretera(String carretera) {
        this.carretera = carretera;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public URL getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(URL urlImage) {
        this.urlImage = urlImage;
    }

    public LocalDateTime getPrimeraInsercion() {
        return primeraInsercion;
    }

    public void setPrimeraInsercion(LocalDateTime primeraInsercion) {
        this.primeraInsercion = primeraInsercion;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public boolean isModificar() {
        return modificar;
    }

    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
