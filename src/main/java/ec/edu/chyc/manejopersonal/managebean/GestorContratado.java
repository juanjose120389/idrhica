/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ContratadoJpaController;
import ec.edu.chyc.manejopersonal.entity.Contratado;
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
@Named(value = "gestorContratado")
@SessionScoped
public class GestorContratado implements Serializable {
    
   // private final PersonaJpaController personaController = new PersonaJpaController();
    private final ContratadoJpaController contratadoController = new ContratadoJpaController();
    private Contratado contratado = new Contratado();
    
   
    private List<Contratado> listContratados = null;

    /**
     * Creates a new instance of GestorContratado
     */
    public GestorContratado() {
        
    }
    @PostConstruct
    public void init() {
        try {
            listContratados = contratadoController.listContratado();
        } catch (Exception ex) {
            Logger.getLogger(GestorContratado.class.getName()).log(Level.SEVERE, null, ex);
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
    public String initCrearContratado() {
        contratado = new Contratado();
                
//        pasantia.setFechaInicio((java.sql.Date) fechaInicio);
  //      pasantia.setFechaFin((java.sql.Date) fechaFin);
       
        
        return "manejoContratado";
    }
    
  
    
    public String actualizarListado() {
        try {
            listContratados = contratadoController.listContratado();            
        } catch (Exception ex) {
            Logger.getLogger(GestorContratado.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static GestorContratado getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorContratado}", GestorContratado.class);
        return (GestorContratado) ex.getValue(context);
    }    
    
    public String guardar() {
        try {
            contratadoController.create(contratado);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorPasantia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public List<Contratado> getListaContratados() {
        return listContratados;
    }

    public void setListaContratados(List<Contratado> listaContratados) {
        this.listContratados = listaContratados;
    }

    public Contratado getContratado() {
        return contratado;
    }

    public void setContratado(Contratado contratado) {
        this.contratado = contratado;
    }



   

}
