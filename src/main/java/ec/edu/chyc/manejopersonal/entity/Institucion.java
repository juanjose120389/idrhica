/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author marcelocaj
 */
@Entity
public class Institucion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    @ManyToMany(mappedBy = "agradecimientosCollection")
    private Set<Articulo> articulosCollection = new HashSet<>();
    
    @OneToMany(mappedBy = "institucion")
    private Collection<Convenio> conveniosCollection = new ArrayList<>();
    
    @OneToMany(mappedBy = "institucion")
    private Collection<Financiamiento> financiamientosCollection = new ArrayList<>();    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public Set<Articulo> getArticulosCollection() {
        return articulosCollection;
    }

    public void setArticulosCollection(Set<Articulo> articulosCollection) {
        this.articulosCollection = articulosCollection;
    }

    public Collection<Convenio> getConveniosCollection() {
        return conveniosCollection;
    }

    public void setConveniosCollection(Collection<Convenio> conveniosCollection) {
        this.conveniosCollection = conveniosCollection;
    }

    public Collection<Financiamiento> getFinanciamientosCollection() {
        return financiamientosCollection;
    }

    public void setFinanciamientosCollection(Collection<Financiamiento> financiamientosCollection) {
        this.financiamientosCollection = financiamientosCollection;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Institucion)) {
            return false;
        }
        Institucion other = (Institucion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Institucion[ id=" + id + " ]";
    }
    
}
