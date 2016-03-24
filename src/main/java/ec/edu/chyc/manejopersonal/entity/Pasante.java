/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 *
 * @author Juan José
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Pasante extends Persona implements Serializable {

    private static final long serialVersionUID = 1L;
   
    private String facultad;

    @ManyToOne
    @JoinColumn(name = "idUniversidad", referencedColumnName = "id")    
    private Universidad universidad;

    private String escuela;
    
    @OneToMany(mappedBy = "pasante")
    private Collection<Pasantia> pasantiasCollection = new ArrayList<>();

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public Universidad getUniversidad() {
        return universidad;
    }

    public void setUniversidad(Universidad universidad) {
        this.universidad = universidad;
    }

    public String getEscuela() {
        return escuela;
    }

    public void setEscuela(String escuela) {
        this.escuela = escuela;
    }

    public Collection<Pasantia> getPasantiasCollection() {
        return pasantiasCollection;
    }

    public void setPasantiasCollection(Collection<Pasantia> pasantiasCollection) {
        this.pasantiasCollection = pasantiasCollection;
    }



    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pasante)) {
            return false;
        }
        return true;
    }

    

}
