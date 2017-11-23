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
import javax.persistence.OneToMany;

/**
 *
 * @author CHYC-DK-005
 */
@Entity
public class Proveedor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruc;
    private String nombre_comercial;
    private String descripcion;
    
    @OneToMany(mappedBy = "proveedor")
    private Collection<Objeto> objetosCollection = new ArrayList<>(); 

    public Proveedor() {
    }

    public Proveedor(Long id, String ruc, String nombre_comercial, String descripcion) {
        this.id = id;
        this.ruc = ruc;
        this.nombre_comercial = nombre_comercial;
        this.descripcion = descripcion;
    }

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
     * @return the ruc
     */
    public String getRuc() {
        return ruc;
    }

    /**
     * @param ruc the ruc to set
     */
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    /**
     * @return the nombre_comercial
     */
    public String getNombre_comercial() {
        return nombre_comercial;
    }

    /**
     * @param nombre_comercial the nombre_comercial to set
     */
    public void setNombre_comercial(String nombre_comercial) {
        this.nombre_comercial = nombre_comercial;
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

    @Override
    public String toString() {
        return String.valueOf(id); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection<Objeto> getObjetosCollection() {
        return objetosCollection;
    }

    public void setObjetosCollection(Collection<Objeto> objetosCollection) {
        this.objetosCollection = objetosCollection;
    }
    
    
    
}
