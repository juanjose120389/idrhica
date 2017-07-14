/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

/**
 *
 * @author marcelocaj
 */
@Entity
public class Convenio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicio;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFin;

    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")        
    private String objetivo = "";
    
    private String titulo = "";
    
    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")    
    private String observaciones;
    
    @ManyToMany(mappedBy = "conveniosCollection")
    @OrderBy("titulo ASC")
    private Set<Proyecto> proyectosCollection = new HashSet<>();    
    
    @Transient
    private List<Articulo> listaArticulos = new ArrayList<>();
    
    @Transient
    private List<Tesis> listaTesis = new ArrayList<>();
    
    /*@ManyToOne
    @JoinColumn(name = "idProyecto", referencedColumnName = "id")    
    private Proyecto proyecto;*/
    
    /*@OneToMany(mappedBy = "convenio")
    private Collection<Articulo> articulosCollection = new ArrayList<>();*/
    
    @ManyToOne
    @JoinColumn(name = "idAdministrador", referencedColumnName = "id")    
    private Persona administrador;    
    
    @ManyToOne
    @JoinColumn(name = "idAdministradorOtro", referencedColumnName = "id")    
    private Persona administradorOtro;   
    
    @ManyToOne
    @JoinColumn(name = "idInstitucion", referencedColumnName = "id")    
    private Institucion institucion;        
    
    @NotNull
    private String archivoConvenio = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
/*
    public Collection<Articulo> getArticulosCollection() {
        return articulosCollection;
    }

    public void setArticulosCollection(Collection<Articulo> articulosCollection) {
        this.articulosCollection = articulosCollection;
    }
*/
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

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Persona getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Persona administrador) {
        this.administrador = administrador;
    }

    public void setAdministradorOtro(Persona administradorOtro) {
        this.administradorOtro = administradorOtro;
    }

    public Persona getAdministradorOtro() {
        return administradorOtro;
    }
    
    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
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
        if (!(object instanceof Convenio)) {
            return false;
        }
        Convenio other = (Convenio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Convenio[ id=" + id + " ]";
    }

    public String getArchivoConvenio() {
        return archivoConvenio;
    }

    public void setArchivoConvenio(String archivoConvenio) {
        this.archivoConvenio = archivoConvenio;
    }

    public Set<Proyecto> getProyectosCollection() {
        return proyectosCollection;
    }

    public void setProyectosCollection(Set<Proyecto> proyectosCollection) {
        this.proyectosCollection = proyectosCollection;
    }

    public List<Articulo> getListaArticulos() {
        return listaArticulos;
    }

    public void setListaArticulos(List<Articulo> listaArticulos) {
        this.listaArticulos = listaArticulos;
    }

    public List<Tesis> getListaTesis() {
        return listaTesis;
    }

    public void setListaTesis(List<Tesis> listaTesis) {
        this.listaTesis = listaTesis;
    }
    
}
