/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.TesistaJpaController;
import ec.edu.chyc.manejopersonal.entity.Tesista;
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
 * @author Juan José
 */
@Named(value = "gestorTesista")
@SessionScoped
public class GestorTesista implements Serializable {
    
   // private final PersonaJpaController personaController = new PersonaJpaController();
    private final TesistaJpaController tesistaController = new TesistaJpaController();
    private Tesista tesista = new Tesista();
    
   
    private List<Tesista> listTesistas = null;

    /**
     * Creates a new instance of GestorContratado
     */
    public GestorTesista() {
        
    }
    @PostConstruct
    public void init() {
        try {
            listTesistas = tesistaController.listTesista();
        } catch (Exception ex) {
            Logger.getLogger(GestorTesista.class.getName()).log(Level.SEVERE, null, ex);
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
    public String initCrearTesista() {
        tesista = new Tesista();
                
//        pasantia.setFechaInicio((java.sql.Date) fechaInicio);
  //      pasantia.setFechaFin((java.sql.Date) fechaFin);
       
        
        return "manejoTesista";
    }
    
   public String initListarTesistas() {
        actualizarListado();
        return "listaTesistas";
    }
    
    public String actualizarListado() {
        try {
            listTesistas = tesistaController.listTesista();            
        } catch (Exception ex) {
            Logger.getLogger(GestorTesista.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static GestorTesista getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorTesista}", GestorTesista.class);
        return (GestorTesista) ex.getValue(context);
    }    
    
    public String guardar() {
        try {
            tesistaController.create(tesista);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorTesista.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public Tesista getTesista() {
        return tesista;
    }

    public void setTesista(Tesista tesista) {
        this.tesista = tesista;
    }

    public List<Tesista> getListTesistas() {
        return listTesistas;
    }

    public void setListTesistas(List<Tesista> listTesistas) {
        this.listTesistas = listTesistas;
    }



    public void setListaContratados(List<Tesista> listaPasantes) {
        this.listTesistas = listaPasantes;
    }

    public Tesista getContratado() {
        return tesista;
    }

    public void setContratado(Tesista tesista) {
        this.tesista = tesista;
    }



   

}
