/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 *
 * @author CHYC-DK-005
 */
@Entity
public class CategoriaObjeto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String descripcion;
    
    @ManyToMany
    @JoinTable(name = "objeto_x_categoria", joinColumns = {
        @JoinColumn(name = "id_categoria", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "id_objeto", referencedColumnName = "id")})
    private Set<Objeto> objetosCollection = new HashSet();

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the objetosCollection
     */
    public Set<Objeto> getObjetosCollection() {
        return objetosCollection;
    }

    /**
     * @param objetosCollection the objetosCollection to set
     */
    public void setObjetosCollection(Set<Objeto> objetosCollection) {
        this.objetosCollection = objetosCollection;
    }
    
    
    
}
