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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author marcelocaj
 */
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"idPersona", "idFirma"})
)
public class PersonaFirma implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idPersona", referencedColumnName = "id")    
    private Persona persona;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "idFirma", referencedColumnName = "id")
    private Firma firma;
    
    @OneToMany(mappedBy = "personaFirma", fetch = FetchType.LAZY)
    private Collection<PersonaArticulo> personasArticulosCollection = new ArrayList<>();    
    
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

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersonaFirma)) {
            return false;
        }
        PersonaFirma other = (PersonaFirma) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.PersonaFirma[ id=" + id + " ]";
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Firma getFirma() {
        return firma;
    }

    public void setFirma(Firma firma) {
        this.firma = firma;
    }

    public Collection<PersonaArticulo> getPersonasArticulosCollection() {
        return personasArticulosCollection;
    }

    public void setPersonasArticulosCollection(Collection<PersonaArticulo> personasArticulosCollection) {
        this.personasArticulosCollection = personasArticulosCollection;
    }
    
}
