/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorMensajes")
@ViewScoped
public class GestorMensajes implements Serializable {

    public static GestorMensajes getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorMensajes}", GestorMensajes.class);
        return (GestorMensajes) ex.getValue(context);
    }        
    
    public GestorMensajes() {
    }
    
    public void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage("formContenido", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }   
    public void mostrarMensajeError(String titulo, String mensaje) {
        FacesContext.getCurrentInstance().addMessage("formContenido", new FacesMessage(FacesMessage.SEVERITY_ERROR, titulo, mensaje));
    }   
    
    public void mostrarMensajeWarn(String mensaje) {
        FacesContext.getCurrentInstance().addMessage("formContenido", new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", mensaje));
    }
    public void mostrarMensajeWarn(String titulo, String mensaje) {
        FacesContext.getCurrentInstance().addMessage("formContenido", new FacesMessage(FacesMessage.SEVERITY_WARN, titulo, mensaje));
    }    
}
