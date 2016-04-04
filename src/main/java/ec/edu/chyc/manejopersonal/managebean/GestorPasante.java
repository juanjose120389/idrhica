/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.PasanteJpaController;
import ec.edu.chyc.manejopersonal.entity.Pasante;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TreeTableColumn;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author CHYC-DK-005
 */
@Named(value = "gestorPasante")
@SessionScoped
public class GestorPasante implements Serializable {
    
   // private final PersonaJpaController personaController = new PersonaJpaController();
    private final PasanteJpaController pasanteController = new PasanteJpaController();
    private Pasante pasante = new Pasante();
    
   
    private List<Pasante> listPasantes = null;

    /**
     * Creates a new instance of GestorContratado
     */
    public GestorPasante() {
        
    }
    @PostConstruct
    public void init() {
        try {
            listPasantes = pasanteController.listPasante();
        } catch (Exception ex) {
            Logger.getLogger(GestorPasante.class.getName()).log(Level.SEVERE, null, ex);
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
    public String initCrearPasante() {
        pasante = new Pasante();
                
//        pasantia.setFechaInicio((java.sql.Date) fechaInicio);
  //      pasantia.setFechaFin((java.sql.Date) fechaFin);
       
        
        return "manejoPasante";
    }
    
  
    
    public String actualizarListado() {
        try {
            listPasantes = pasanteController.listPasante();            
        } catch (Exception ex) {
            Logger.getLogger(GestorPasante.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static GestorPasante getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPasante}", GestorPasante.class);
        return (GestorPasante) ex.getValue(context);
    }    
    
    public String guardar() {
        try {
            pasanteController.create(pasante);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorPasantia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public List<Pasante> getListaPasantes() {
        return listPasantes;
    }

    public void setListaContratados(List<Pasante> listaPasantes) {
        this.listPasantes = listaPasantes;
    }

    public Pasante getContratado() {
        return pasante;
    }

    public void setContratado(Pasante pasante) {
        this.pasante = pasante;
    }



   

}
