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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

/**
 *
 * @author marcelocaj
 */
@Entity
public class Proyecto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "idDirector", referencedColumnName = "id")    
    private Persona director;

    @ManyToOne
    @JoinColumn(name = "idCodirector", referencedColumnName = "id")    
    private Persona codirector;

    @Transient
    private Integer duracion;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicio;
    
    //fecha real de finalización del proyecto, puede ser posterior a la fecha indicada en el documento (en 
    // este caso sería debido a una ampliación).
    //Para realizar consultas de proyectos terminados, se debe tomar en cuenta siempre esta fecha
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFin;

    //fecha fin del proyecto de acuerdo al documento
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFinEnDocumento;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")    
    @NotNull
    private String observaciones = "";
    
    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")    
    @NotNull
    private String resumen = "";
    
    //@OneToMany(mappedBy = "proyecto")
    //private Collection<Contrato> contratosCollection = new ArrayList<>();           

    
    @ManyToMany
    @JoinTable(name = "proyectoConvenio", joinColumns = {
        @JoinColumn(name = "idProyecto", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "idConvenio", referencedColumnName = "id")})
    private Set<Convenio> conveniosCollection = new HashSet();
    
    /*@OneToMany(mappedBy = "proyecto")
    private Collection<Convenio> conveniosCollection = new ArrayList<>();*/

    @OneToMany(mappedBy = "proyecto")
    private Collection<Pasantia> pasantiasCollection = new ArrayList<>();
    
    @OneToMany(mappedBy = "proyecto")
    private Collection<Financiamiento> financiamientosCollection = new ArrayList<>();    
    
    @ManyToMany
    @JoinTable(name = "proyectoTesis", joinColumns = {
        @JoinColumn(name = "idProyecto", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "idTesis", referencedColumnName = "id")})
    private Set<Tesis> tesisCollection = new HashSet();

    @ManyToMany
    @JoinTable(name = "proyectoArticulo", joinColumns = {
        @JoinColumn(name = "idProyecto", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "idArticulo", referencedColumnName = "id")})
    private Set<Articulo> articulosCollection = new HashSet();    
    
    @ManyToMany
    @JoinTable(name = "proyectoContrato", joinColumns = {
        @JoinColumn(name = "idProyecto", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "idContrato", referencedColumnName = "id")})
    private Set<Contrato> contratosCollection = new HashSet();   
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Contrato> getContratosCollection() {
        return contratosCollection;
    }

    public void setContratosCollection(Set<Contrato> contratosCollection) {
        this.contratosCollection = contratosCollection;
    }

    public Persona getDirector() {
        return director;
    }

    public void setDirector(Persona director) {
        this.director = director;
    }

    public Persona getCodirector() {
        return codirector;
    }

    public void setCodirector(Persona codirector) {
        this.codirector = codirector;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public Collection<Financiamiento> getFinanciamientosCollection() {
        return financiamientosCollection;
    }

    public void setFinanciamientosCollection(Collection<Financiamiento> financiamientosCollection) {
        this.financiamientosCollection = financiamientosCollection;
    }

    public Collection<Pasantia> getPasantiasCollection() {
        return pasantiasCollection;
    }

    public void setPasantiasCollection(Collection<Pasantia> pasantiasCollection) {
        this.pasantiasCollection = pasantiasCollection;
    }

    public Set<Tesis> getTesisCollection() {
        return tesisCollection;
    }

    public void setTesisCollection(Set<Tesis> tesisCollection) {
        this.tesisCollection = tesisCollection;
    }

    public Set<Articulo> getArticulosCollection() {
        return articulosCollection;
    }

    public void setArticulosCollection(Set<Articulo> articulosCollection) {
        this.articulosCollection = articulosCollection;
    }    
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Proyecto)) {
            return false;
        }
        Proyecto other = (Proyecto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Proyecto[ id=" + id + " ]";
    }

    public Date getFechaFinEnDocumento() {
        return fechaFinEnDocumento;
    }

    public void setFechaFinEnDocumento(Date fechaFinEnDocumento) {
        this.fechaFinEnDocumento = fechaFinEnDocumento;
    }

    public Set<Convenio> getConveniosCollection() {
        return conveniosCollection;
    }

    public void setConveniosCollection(Set<Convenio> conveniosCollection) {
        this.conveniosCollection = conveniosCollection;
    }
    
}
