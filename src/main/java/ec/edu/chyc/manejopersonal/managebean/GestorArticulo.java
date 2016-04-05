/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ArticuloJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Persona;
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
 * @author marcelocaj
 */
@Named(value = "gestorArticulo")
@SessionScoped
public class GestorArticulo implements Serializable {

    private final ArticuloJpaController articuloController = new ArticuloJpaController();
    
    private List<Articulo> listaArticulos = new ArrayList<>();
    
    public GestorArticulo() {
    }
    
    @PostConstruct
    public void init() {
        
    }
    public String convertirListaPersonas(List<Persona> listaConvertir) {
        String r = "";
        for (Persona per : listaConvertir) {
            r += String.format("%s %s, ", per.getApellidos(), per.getNombres());
        }
        if (!r.isEmpty()) {
            r = r.substring(0, r.length() - 3);
        }
        
        return r;
    }
    
    public static GestorArticulo getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorArticulo}",GestorArticulo.class);
        return (GestorArticulo)ex.getValue(context);
    }
    
    public void actualizarListaArticulos() {
        try {
            listaArticulos = articuloController.listArticulos();
        } catch (Exception ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public String initListarArticulos() {
        actualizarListaArticulos();
        return "listaArticulos";
    }

    public List<Articulo> getListaArticulos() {
        return listaArticulos;
    }

    public void setListaArticulos(List<Articulo> listaArticulos) {
        this.listaArticulos = listaArticulos;
    }

}
