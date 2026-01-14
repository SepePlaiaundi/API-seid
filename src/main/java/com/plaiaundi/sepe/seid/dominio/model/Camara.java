package com.plaiaundi.sepe.seid.dominio.model;

import java.net.URL;

import org.hibernate.annotations.IdGeneratorType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="camaras")
public class Camara{
    
    // Atributos
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

    /** Constructor explicito */
    public Camara(
        String id, 
        String direccion, 
        String nombre, 
        String kilometro, 
        String latitud, 
        String longitud,
        String carretera
    ) {
        this.id = id;
        this.direccion = direccion;
        this.nombre = nombre;
        this.kilometro = kilometro;
        this.latitud = latitud;
        this.longitud = longitud;
        this.carretera = carretera;
    }

    /** Contructor implicito */
    public Camara() {}

    // Getters y setters 
    public String getId()                       { return id;                    }
    public void setId(String id)                { this.id = id;                 }

    public String getDireccion()                { return direccion;             }
    public void setDireccion(String direccion)  { this.direccion = direccion;   }

    public String getNombre()                   { return nombre;                }
    public void setNombre(String nombre)        { this.nombre = nombre;         }

    public String getKilometro()                { return kilometro;             }
    public void setKilometro(String kilometro)  { this.kilometro = kilometro;   }

    public String getLatitud()                  { return latitud;               }
    public void setLatitud(String latitud)      { this.latitud = latitud;       }

    public String getLongitud()                 { return longitud;              }
    public void setLongitud(String longitud)    { this.longitud = longitud;     }

    public String getCarretera()                { return carretera;             }
    public void setCarretera(String carretera)  { this.carretera = carretera;   }

    public URL getUrlImage()                    { return urlImage;              }
    public void setUrlImage(URL urlImagen)      { this.urlImage = urlImagen;    }

}
