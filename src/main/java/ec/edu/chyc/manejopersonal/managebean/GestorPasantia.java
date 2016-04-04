/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.PasantiaJpaController;
import ec.edu.chyc.manejopersonal.controller.PersonaJpaController;
import ec.edu.chyc.manejopersonal.entity.Pasantia;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TreeTableColumn;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorPasantia")
@SessionScoped
public class GestorPasantia implements Serializable {
    
    private final PersonaJpaController personaController = new PersonaJpaController();
    private final PasantiaJpaController pasantiaController = new PasantiaJpaController();
    private Pasantia pasantia = new Pasantia();
    
    private Date fechaInicio = new Date();
    
    private Date fechaFin = new Date();
    
    
    
    private List<Pasantia> listPasantes = null;

    /**
     * Creates a new instance of GestorPersona
     */
    public GestorPasantia() {
        
    }
    @PostConstruct
    public void init() {
        try {
            listPasantes = pasantiaController.listPasantias();
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    public void onCellEdit(TreeTableColumn.CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
         
        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }    
    public String initCrearPasantia() {
        pasantia = new Pasantia();
        fechaInicio = new Date();
        fechaFin = new Date();
        
        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(fechaInicio);        
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.add(Calendar.YEAR, -50);
        //fechaFin = calendar.getTime();
        
//        pasantia.setFechaInicio((java.sql.Date) fechaInicio);
  //      pasantia.setFechaFin((java.sql.Date) fechaFin);
        
        

        
      
        
        return "manejoPasantia";
    }
    
  
    
    public String actualizarListado() {
        try {
            listPasantes = pasantiaController.listPasantias();            
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static GestorPersona getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPersona}", GestorPersona.class);
        return (GestorPersona) ex.getValue(context);
    }    
    
    public String guardar() {
        try {
            pasantiaController.create(pasantia);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorPasantia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public List<Pasantia> getListaPasantias() {
        return listPasantes;
    }

    public void setListaPasantias(List<Pasantia> listaPasantia) {
        this.listPasantes = listaPasantia;
    }

    public Pasantia getPasantia() {
        return pasantia;
    }

    public void setPersona(Pasantia pasantia) {
        this.pasantia = pasantia;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

   

}
