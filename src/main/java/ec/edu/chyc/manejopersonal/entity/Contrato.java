/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author Marcelo
 */
@Entity
public class Contrato implements Serializable {

    public enum TipoContrato {
        RRHH,//los profesores solo pueden tener este tipo de contrato
        SERCOP,
        SERV_PROFESIONALES
    }
    public enum TipoProfesor {
        CONTRATADO,
        PRINCIPAL,
        AGREGADO
    }
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idPersona", referencedColumnName = "id")
    private Persona persona;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicio;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFin;
    private int numeroContrato;
    @ManyToOne
    @JoinColumn(name = "idAdminContrato", referencedColumnName = "id")
    private Persona administrador;
    
    @ManyToOne
    @JoinColumn(name = "idProyecto", referencedColumnName = "id")
    private Proyecto proyecto;
        
    private String cargo;
    private String actividadPrincipal;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaFinDocumento; //fecha cuando termine el contrato de acuerdo al documento
    
    private String facultad;
    private String tipo; // C=Contratado, T=Titular
    
    private String archivoContrato;
    
    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getActividadPrincipal() {
        return actividadPrincipal;
    }

    public void setActividadPrincipal(String actividadPrincipal) {
        this.actividadPrincipal = actividadPrincipal;
    }


    public Persona getPersona() {
        return persona;
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

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
    
     public Persona getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Persona administrador) {
        this.administrador = administrador;
    }
    
      public int getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(int numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }   
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaFinDocumento() {
        return fechaFinDocumento;
    }

    public void setFechaFinDocumento(Date fechaFinDocumento) {
        this.fechaFinDocumento = fechaFinDocumento;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getArchivoContrato() {
        return archivoContrato;
    }

    public void setArchivoContrato(String archivoContrato) {
        this.archivoContrato = archivoContrato;
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
        if (!(object instanceof Contrato)) {
            return false;
        }
        Contrato other = (Contrato) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.chyc.manejopersonal.entity.Contrato[ id=" + id + " ]";
    }
    
}
