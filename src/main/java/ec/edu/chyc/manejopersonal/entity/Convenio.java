/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
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
    
    private String titulo;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    @Type(type = "text")    
    private String observaciones;
    
    @ManyToOne
    @JoinColumn(name = "idProyecto", referencedColumnName = "id")    
    private Proyecto proyecto;
    
    /*@OneToMany(mappedBy = "convenio")
    private Collection<Articulo> articulosCollection = new ArrayList<>();*/
    
    @ManyToOne
    @JoinColumn(name = "idAdministrador", referencedColumnName = "id")    
    private Persona administrador;    
    
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

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
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
    
}
