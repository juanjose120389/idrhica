/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

/**
 *
 * @author Juan José
 */
@Entity
public class Pasantia implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer numeroHoras;
    
    private String tema;

    private Integer cicloPasante;
    
    private String facultad;
    
    private String escuela;
    
    @ManyToOne
    @JoinColumn(name = "idCoordinador", referencedColumnName = "id")   
    private Persona coordinador;
    
    @ManyToOne
    @JoinColumn(name = "idTutor", referencedColumnName = "id")   
    private Persona tutor;
    
    @ManyToOne
    @JoinColumn(name = "idPasante", referencedColumnName = "id")   
    private Persona pasante;
   
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicio;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFin;    
    
    @Lob
    @Column(columnDefinition = "TEXT default ''")
    @Type(type = "text")
    @NotNull
    private String resumen = "";
    
    @ManyToOne
    @JoinColumn(name = "idUniversidad", referencedColumnName = "id")   
    private Universidad universidad;    
    
    @ManyToOne
    @JoinColumn(name = "idProyecto", referencedColumnName = "id")
    private Proyecto proyecto;
    
    
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

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public Persona getCoordinador() {
        return coordinador;
    }

    public void setCoordinador(Persona coordinador) {
        this.coordinador = coordinador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Integer getNumeroHoras() {
        return numeroHoras;
    }

    public void setNumeroHoras(Integer numeroHoras) {
        this.numeroHoras = numeroHoras;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pasantia other = (Pasantia) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Integer getCicloPasante() {
        return cicloPasante;
    }

    public void setCicloPasante(Integer cicloPasante) {
        this.cicloPasante = cicloPasante;
    }

    public Universidad getUniversidad() {
        return universidad;
    }

    public void setUniversidad(Universidad universidad) {
        this.universidad = universidad;
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

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Persona getPasante() {
        return pasante;
    }

    public void setPasante(Persona pasante) {
        this.pasante = pasante;
    }

    public Persona getTutor() {
        return tutor;
    }

    public void setTutor(Persona tutor) {
        this.tutor = tutor;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }
}
