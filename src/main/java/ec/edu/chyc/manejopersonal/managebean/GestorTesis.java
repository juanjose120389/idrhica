/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.TesisJpaController;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 *
 * @author Juan José
 */
@Named(value = "gestorTesis")
@SessionScoped
public class GestorTesis implements Serializable {

    private final TesisJpaController tesisController = new TesisJpaController();
    
    private List<Tesis> listaTesis = new ArrayList<>();
    
    public GestorTesis() {
    }
    
    @PostConstruct
    public void init() {
        
    }
    
    public static GestorTesis getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorTesis}",GestorTesis.class);
        return (GestorTesis)ex.getValue(context);
    }
    
    public void actualizarListaTesis() {
        try {
            listaTesis = tesisController.listTesis();
        } catch (Exception ex) {
            Logger.getLogger(GestorTesis.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public String initListarTesis() {
        actualizarListaTesis();
        return "listaTesis";
    }

    public List<Tesis> getListaTesis() {
        return listaTesis;
    }

    public void setListaTesis(List<Tesis> listaTesis) {
        this.listaTesis = listaTesis;
    }

}
