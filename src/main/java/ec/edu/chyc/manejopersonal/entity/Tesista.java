/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 *
 * @author Juan José
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Tesista extends Persona implements Serializable {

    private static final long serialVersionUID = 1L;
   
    private String facultad;   
    
    private String escuela;
    
    @ManyToOne
    @JoinColumn(name = "idUniversidad", referencedColumnName = "id")
    private Universidad universidad;
    
    @ManyToOne
    @JoinColumn(name = "idTesis", referencedColumnName = "id")
    private Tesis tesis;

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

    public Tesis getTesis() {
        return tesis;
    }

    public void setTesis(Tesis tesis) {
        this.tesis = tesis;
    }

    public Universidad getUniversidad() {
        return universidad;
    }

    public void setUniversidad(Universidad universidad) {
        this.universidad = universidad;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        
        return object instanceof Tesista && Objects.equals(((Tesista)object).getId(), getId());
    }   

}
