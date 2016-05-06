/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import org.hibernate.annotations.Type;

/**
 *
 * @author marcelocaj
 */
@Entity
public class Tesis implements Serializable {

    public enum TipoTesis {
        PREGRADO,
        MASTER,
        DOCTORADO
    }
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "idDirectorTesis", referencedColumnName = "id")
    private Persona director;
    
    @ManyToMany(mappedBy = "tesisComoCodirectorCollection")
    private Set<Persona> codirectoresCollection = new HashSet<>();    

    @ManyToMany(mappedBy = "tesisComoTutorCollection")
    private Set<Persona> tutoresCollection = new HashSet<>();
    
    @ManyToMany(mappedBy = "tesisCollection")
    private Set<Proyecto> proyectosCollection = new HashSet<>();
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicio;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFin;

    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")    
    private String observaciones;

    //private String tipo;//M=Master, D=Doctorado, P=Pregrado
    @Enumerated(EnumType.STRING)
    private TipoTesis tipo;

    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")
    private String resumen;

    private String actaAprobacion;
    
    private String archivoActaAprobacion;
    
    @ManyToMany(mappedBy = "tesisCollection")
    private Set<Persona> autoresCollection = new HashSet<>();
    
    private String facultad;
    
    private String escuela;
    
    @ManyToOne
    @JoinColumn(name = "idUniversidad", referencedColumnName = "id")
    private Universidad universidad;    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getDirector() {
        return director;
    }

    public void setDirector(Persona director) {
        this.director = director;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tesis)) {
            return false;
        }
        Tesis other = (Tesis) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Tesis[ id=" + id + " ]";
    }

    public String getArchivoActaAprobacion() {
        return archivoActaAprobacion;
    }

    public void setArchivoActaAprobacion(String archivoActaAprobacion) {
        this.archivoActaAprobacion = archivoActaAprobacion;
    }    

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getEscuela() {
        return escuela;
    }

    public void setEscuela(String escuela) {
        this.escuela = escuela;
    }

    public Universidad getUniversidad() {
        return universidad;
    }

    public void setUniversidad(Universidad universidad) {
        this.universidad = universidad;
    }

    public Set<Persona> getAutoresCollection() {
        return autoresCollection;
    }

    public void setAutoresCollection(Set<Persona> autoresCollection) {
        this.autoresCollection = autoresCollection;
    }

    public String getActaAprobacion() {
        return actaAprobacion;
    }

    public void setActaAprobacion(String actaAprobacion) {
        this.actaAprobacion = actaAprobacion;
    }

    public Set<Persona> getCodirectoresCollection() {
        return codirectoresCollection;
    }

    public void setCodirectoresCollection(Set<Persona> codirectoresCollection) {
        this.codirectoresCollection = codirectoresCollection;
    }

    public Set<Persona> getTutoresCollection() {
        return tutoresCollection;
    }

    public void setTutoresCollection(Set<Persona> tutoresCollection) {
        this.tutoresCollection = tutoresCollection;
    }

    public Set<Proyecto> getProyectosCollection() {
        return proyectosCollection;
    }

    public void setProyectosCollection(Set<Proyecto> proyectosCollection) {
        this.proyectosCollection = proyectosCollection;
    }

    public TipoTesis getTipo() {
        return tipo;
    }

    public void setTipo(TipoTesis tipo) {
        this.tipo = tipo;
    }
    
}
