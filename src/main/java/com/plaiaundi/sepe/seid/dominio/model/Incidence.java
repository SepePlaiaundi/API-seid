package com.plaiaundi.sepe.seid.dominio.model;

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

import lombok.NoArgsConstructor;

@Entity
@Table(name = "incidencias", uniqueConstraints = {
        @UniqueConstraint(name = "uk_incidencia_id_resource", columnNames = { "id", "recurso_id" })
})
@NoArgsConstructor
public class Incidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_model")
    @JsonIgnore
    private Integer idModel;

    @Column(name = "id")
    private int id;

    @ManyToOne(cascade = { CascadeType.MERGE })
    @JoinColumn(name = "recurso_id")
    private Recurso recurso;

    private String provincia;
    private String causa;
    private LocalDateTime fecIni;
    private String carretera;
    private String direccion;
    private double latitud;
    private double longitud;
    private String ciudad;
    private LocalDateTime fecFin;
    private String nivel;
    private String tipo;
    private String descripcion;

    private LocalDateTime primeraInsercion = LocalDateTime.now();
    private LocalDateTime ultimaActualizacion;

    @JsonIgnore
    private boolean modificar = false;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVA; // Activo o Eliminado

    @Transient
    @JsonProperty(value = "new", access = JsonProperty.Access.READ_ONLY)
    private boolean isNew;

    @Transient
    @JsonProperty(value = "recursoId", access = JsonProperty.Access.WRITE_ONLY)
    public void setRecursoId(Integer id) {
        if (id != null) {
            this.recurso = new Recurso();
            this.recurso.setId(id);
        }
    }

    // Manual Getters and Setters
    public Integer getIdModel() {
        return idModel;
    }

    public void setIdModel(Integer idModel) {
        this.idModel = idModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public LocalDateTime getFecIni() {
        return fecIni;
    }

    public void setFecIni(LocalDateTime fecIni) {
        this.fecIni = fecIni;
    }

    public String getCarretera() {
        return carretera;
    }

    public void setCarretera(String carretera) {
        this.carretera = carretera;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalDateTime getFecFin() {
        return fecFin;
    }

    public void setFecFin(LocalDateTime fecFin) {
        this.fecFin = fecFin;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public boolean isNew() {
        if (ultimaActualizacion == null) {
            return true;
        }
        return ultimaActualizacion.isAfter(LocalDateTime.now().minusHours(2));
    }

    public void setNew(boolean aNew) {
        // Ignored, calculated dynamically
    }
}
