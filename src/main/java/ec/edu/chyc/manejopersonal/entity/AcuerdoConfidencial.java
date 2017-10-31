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
import org.hibernate.annotations.Type;

/**
 *
 * @author CHYC-DK-005
 */
@Entity
public class AcuerdoConfidencial implements Serializable{
    
    private static final long serialVersionUID = 1L;      
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_beneficiario", referencedColumnName = "id")
    private Persona beneficiario;
    
    @Lob
    @Column(columnDefinition = "TEXT", name="objeto_acuerdo")
    @Type(type = "text")         
    private String objetoAcuerdo;
    
    @Lob
    @Column(columnDefinition = "TEXT", name = "descripcion_informacion")
    @Type(type = "text")  
    private String descripcionInformacion;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "fecha_inicio")
    private Date fechaInicio;
    
    @Column(name = "archivo_acuerdo_conf")
    private String archivoAcuerdoConf = "";//nombre del archivo subido que corresponde al artículo


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
     * @return the beneficiario
     */
    public Persona getBeneficiario() {
        return beneficiario;
    }

    /**
     * @param beneficiario the beneficiario to set
     */
    public void setBeneficiario(Persona beneficiario) {
        this.beneficiario = beneficiario;
    }

    /**
     * @return the objetoAcuerdo
     */
    public String getObjetoAcuerdo() {
        return objetoAcuerdo;
    }

    /**
     * @param objetoAcuerdo the objetoAcuerdo to set
     */
    public void setObjetoAcuerdo(String objetoAcuerdo) {
        this.objetoAcuerdo = objetoAcuerdo;
    }

    /**
     * @return the descripcionInformacion
     */
    public String getDescripcionInformacion() {
        return descripcionInformacion;
    }

    /**
     * @param descripcionInformacion the descripcionInformacion to set
     */
    public void setDescripcionInformacion(String descripcionInformacion) {
        this.descripcionInformacion = descripcionInformacion;
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setArchivoAcuerdoConf(String archivoAcuerdoConf) {
        this.archivoAcuerdoConf = archivoAcuerdoConf;
    }

    public String getArchivoAcuerdoConf() {
        return archivoAcuerdoConf;
    }    
    
}
