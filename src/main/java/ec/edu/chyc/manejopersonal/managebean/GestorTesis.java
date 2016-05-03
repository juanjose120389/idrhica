/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.TesisJpaController;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Juan José
 */
@Named(value = "gestorTesis")
@SessionScoped
public class GestorTesis implements Serializable {

    private final TesisJpaController tesisController = new TesisJpaController();
     private Tesis tesis = new Tesis();
    private List<Tesis> listaTesis = new ArrayList<>();
    private List<Persona> listaAutoresTesis = new ArrayList<>();
    
    public GestorTesis() {
    }

    public List<Persona> getListaAutores() {
        return listaAutoresTesis;
    }

    public void setListaAutores(List<Persona> listaAutores) {
        this.listaAutoresTesis = listaAutores;
    }

    public Tesis getTesis() {
        return tesis;
    }

    public void setTesis(Tesis tesis) {
        this.tesis = tesis;
    }
    
    @PostConstruct
    public void init() {
        
    }
    public String initCrearTesis() {
        tesis = new Tesis();
        GestorContrato.getInstance().actualizarListaContrato();
        listaAutoresTesis.clear();

        return "manejoTesis";
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
     public void quitarAutor(Persona personaQuitar) {
        listaAutoresTesis.remove(personaQuitar);
    }
     public void onPersonaChosen(SelectEvent event) {
        List <Persona> listaPersonasSel = (List<Persona>) event.getObject();
        //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Car Selected", "Id:" + car.getId());
        if (listaPersonasSel != null) {
            for (Persona per : listaPersonasSel) {
                if (listaAutoresTesis.indexOf(per) < 0) {
                    listaAutoresTesis.add(per);
                }
            }
            
            //listaAutores.addAll(listaPersonasSel);
            RequestContext.getCurrentInstance().update("formContenido:dtAutoresTesis");
        }
    }
    
    public void agregarAutor() {
        Persona personaNueva = new Persona();
        
        Map<String,Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaPersonas.getInstance().clearListaPersonasSel();
        RequestContext.getCurrentInstance().openDialog("dialogListaPersonas", options, null);
    }
    
    public String convertirListaPersonas(List<Persona> listaConvertir) {
        String r = "";
        for (Persona per : listaConvertir) {
            r += String.format("%s %s, ", per.getApellidos(), per.getNombres());
        }
        if (!r.isEmpty()) {
            r = r.substring(0, r.length() - 2);
        }
        
        return r;
    }
    
    public String initListarTesis() {
        actualizarListaTesis();
       
       // listaAutores.clear();
        return "listaTesis";
    }

    public List<Tesis> getListaTesis() {
        return listaTesis;
    }

    public void setListaTesis(List<Tesis> listaTesis) {
        this.listaTesis = listaTesis;
    }
    
    public String guardar() {
        try {
            tesis.setAutoresCollection(new HashSet(listaAutoresTesis));
            tesisController.create(tesis);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorTesis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

}
