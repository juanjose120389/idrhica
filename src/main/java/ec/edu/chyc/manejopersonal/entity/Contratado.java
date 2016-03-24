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
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 *
 * @author marcelocaj
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Contratado extends Persona implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String cargo;
    private String actividadPrincipal;

    @OneToMany(mappedBy = "coordinador")
    private Collection<Pasantia> pasantiasCollection = new ArrayList<>();    
    
    
    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getActividadPrincipal() {
        return actividadPrincipal;
    }

    public void setActividadPrincipal(String actividadPrincipal) {
        this.actividadPrincipal = actividadPrincipal;
    }

    public Collection<Pasantia> getPasantiasCollection() {
        return pasantiasCollection;
    }

    public void setPasantiasCollection(Collection<Pasantia> pasantiasCollection) {
        this.pasantiasCollection = pasantiasCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contratado)) {
            return false;
        }
        Contratado other = (Contratado) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Contratado[ id=" + getId() + " ]";
    }
    
}
