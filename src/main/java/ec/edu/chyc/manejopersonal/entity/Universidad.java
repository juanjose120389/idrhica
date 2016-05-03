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
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author marcelocaj
 */
@Entity
public class Universidad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String ciudad;
    private String pais;
    
    @OneToMany(mappedBy = "universidad")
    private Collection<PersonaTitulo> personaTitulosCollection = new ArrayList<>();
    
    @OneToMany(mappedBy = "universidad")
    private Collection<Pasantia> pasantiasCollection = new ArrayList<>();

    @OneToMany(mappedBy = "universidad")
    private Collection<Tesis> tesisCollection = new ArrayList<>();    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<PersonaTitulo> getPersonaTitulosCollection() {
        return personaTitulosCollection;
    }

    public void setPersonaTitulosCollection(Collection<PersonaTitulo> personaTitulosCollection) {
        this.personaTitulosCollection = personaTitulosCollection;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Collection<Pasantia> getPasantiasCollection() {
        return pasantiasCollection;
    }

    public void setPasantiasCollection(Collection<Pasantia> pasantiasCollection) {
        this.pasantiasCollection = pasantiasCollection;
    }

    public Collection<Tesis> getTesisCollection() {
        return tesisCollection;
    }

    public void setTesisCollection(Collection<Tesis> tesisCollection) {
        this.tesisCollection = tesisCollection;
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
        if (!(object instanceof Universidad)) {
            return false;
        }
        Universidad other = (Universidad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Universidad[ id=" + id + " ]";
    }    
}
