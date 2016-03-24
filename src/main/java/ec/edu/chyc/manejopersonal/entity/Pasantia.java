/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Juan José
 */
@Entity
public class Pasantia implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numeroHoras;
    
    private String tema;
    
    @ManyToOne
    @JoinColumn(name = "idContratadoCoordinador", referencedColumnName = "id")   
    private Contratado coordinador;
    
    @ManyToOne
    @JoinColumn(name = "idPasante", referencedColumnName = "id")   
    private Pasante pasante;
    
    private Date fechaInicio;
    
    private Date fechaFin;    
    
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

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public Contratado getCoordinador() {
        return coordinador;
    }

    public void setCoordinador(Contratado coordinador) {
        this.coordinador = coordinador;
    }

    public Pasante getPasante() {
        return pasante;
    }

    public void setPasante(Pasante pasante) {
        this.pasante = pasante;
    }


    public int getNumeroHoras() {
        return numeroHoras;
    }

    public void setNumeroHoras(int numeroHoras) {
        this.numeroHoras = numeroHoras;
    }

   

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pasantia)) {
            return false;
        }
        
        return true;
    }

   

}
