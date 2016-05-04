/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

/**
 *
 * @author marcelocaj
 */
@Entity
public class Articulo implements Serializable {

    public enum TipoArticulo {
        LIBRO,
        TESIS,
        SCOPUS,
        NOTA_TECNICA
    }
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    private String tema;

    @NotNull
    private String revista = "";
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPublicacion;

    @ManyToOne
    @JoinColumn(name = "idAutorPrincipal", referencedColumnName = "id")
    private Persona autorPrincipal;

    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")        
    @NotNull
    private String resumen = "";
    
    @NotNull
    private String enlace = "";
    
    @Enumerated(EnumType.STRING)
    private TipoArticulo tipo;
    
    private Float factorImpacto;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")
    @NotNull
    private String referenciaBib = "";

    @NotNull
    private String archivoArticulo = "";//nombre del archivo subido que corresponde al artículo
    /*
    @ManyToMany(mappedBy = "articulosCollection")
    private Set<Persona> autoresCollection = new HashSet<>();
    */
    @OneToMany(mappedBy = "articulo", fetch = FetchType.LAZY)
    private Collection<PersonaArticulo> personasArticuloCollection = new ArrayList<>();
    
    @ManyToMany(mappedBy = "articulosCollection")
    private Set<Proyecto> proyectosCollection = new HashSet<>();    

    @ManyToMany
    @JoinTable(name = "articuloInstitucion", joinColumns = {
        @JoinColumn(name = "idArticulo", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "idInstitucion", referencedColumnName = "id")})
    private Set<Institucion> agradecimientosCollection = new HashSet();
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }

    public Float getFactorImpacto() {
        return factorImpacto;
    }

    public void setFactorImpacto(Float factorImpacto) {
        this.factorImpacto = factorImpacto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getRevista() {
        return revista;
    }

    public void setRevista(String revista) {
        this.revista = revista;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getReferenciaBib() {
        return referenciaBib;
    }

    public void setReferenciaBib(String referenciaBib) {
        this.referenciaBib = referenciaBib;
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
        if (!(object instanceof Articulo)) {
            return false;
        }
        Articulo other = (Articulo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Articulo[ id=" + id + " ]";
    }

    public Persona getAutorPrincipal() {
        return autorPrincipal;
    }

    public void setAutorPrincipal(Persona autorPrincipal) {
        this.autorPrincipal = autorPrincipal;
    }

    public TipoArticulo getTipo() {
        return tipo;
    }

    public void setTipo(TipoArticulo tipo) {
        this.tipo = tipo;
    }

    public String getArchivoArticulo() {
        return archivoArticulo;
    }

    public void setArchivoArticulo(String archivoArticulo) {
        this.archivoArticulo = archivoArticulo;
    }

    public Set<Proyecto> getProyectosCollection() {
        return proyectosCollection;
    }

    public void setProyectosCollection(Set<Proyecto> proyectosCollection) {
        this.proyectosCollection = proyectosCollection;
    }

    public Set<Institucion> getAgradecimientosCollection() {
        return agradecimientosCollection;
    }

    public void setAgradecimientosCollection(Set<Institucion> agradecimientosCollection) {
        this.agradecimientosCollection = agradecimientosCollection;
    }

    public Collection<PersonaArticulo> getPersonasArticuloCollection() {
        return personasArticuloCollection;
    }

    public void setPersonasArticuloCollection(Collection<PersonaArticulo> personasArticuloCollection) {
        this.personasArticuloCollection = personasArticuloCollection;
    }
    
}
