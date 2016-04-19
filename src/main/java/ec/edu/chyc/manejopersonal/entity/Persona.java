/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author Juan José
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombres;
    private String apellidos;
    private String identificacion;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNacimiento;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVinculacion;
    private String correo;
    private String celular;
    private String skype;
    @Column(length = 2)
    private String genero; //M=Masculino, F=Femenino
    private String direccion;
    private Boolean activo = true;

    @OneToMany(mappedBy = "persona")
    private Collection<Contrato> contratosCollection = new ArrayList<>();

    @OneToMany(mappedBy = "autorPrincipal")
    private Collection<Articulo> articulosPrincipalCollection = new ArrayList<>();    
    
    @OneToMany(mappedBy = "persona")
    private Collection<PersonaTitulo> personaTitulosCollection = new ArrayList<>();
  
    @ManyToMany
    @JoinTable(name = "personaArticulo", joinColumns = {
        @JoinColumn(name = "idPersona", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "idArticulo", referencedColumnName = "id")})
    private Collection<Articulo> articulosCollection = new ArrayList();
    
    public Collection<Contrato> getContratosCollection() {
        return contratosCollection;
    }

    public void setContratosCollection(Collection<Contrato> contratosCollection) {
        this.contratosCollection = contratosCollection;
    }

    public Collection<Articulo> getArticulosCollection() {
        return articulosCollection;
    }

    public void setArticulosCollection(Collection<Articulo> articulosCollection) {
        this.articulosCollection = articulosCollection;
    }

    public Collection<PersonaTitulo> getPersonaTitulosCollection() {
        return personaTitulosCollection;
    }

    public void setPersonaTitulosCollection(Collection<PersonaTitulo> personaTitulosCollection) {
        this.personaTitulosCollection = personaTitulosCollection;
    }
    
    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombre) {
        this.nombres = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Date getFechaVinculacion() {
        return fechaVinculacion;
    }

    public void setFechaVinculacion(Date fechaVinculacion) {
        this.fechaVinculacion = fechaVinculacion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }


    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

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
        if (!(object instanceof Persona)) {
            return false;
        }
        Persona other = (Persona) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.ucuenca.manejopersonal.entity.Persona[ id=" + id + " ]";
    }

    public Collection<Articulo> getArticulosPrincipalCollection() {
        return articulosPrincipalCollection;
    }

    public void setArticulosPrincipalCollection(Collection<Articulo> articulosPrincipalCollection) {
        this.articulosPrincipalCollection = articulosPrincipalCollection;
    }

    public Persona() {        
    }

}
