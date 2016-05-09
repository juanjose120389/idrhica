/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.PasantiaJpaController;
import ec.edu.chyc.manejopersonal.entity.Pasantia;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorPasantia")
@SessionScoped
public class GestorPasantia implements Serializable {

    private final PasantiaJpaController pasantiaController = new PasantiaJpaController();
    private Pasantia pasantia = new Pasantia();

    private List<Pasantia> listaPasantias = null;

    private boolean modoModificar = false;

    public GestorPasantia() {

    }

    @PostConstruct
    public void init() {
        actualizarListado();
    }

    public String initListarPasantias() {
        actualizarListado();
        return "listaPasantias";
    }

    public String initModificarPasantia(Long id) {
        inicializarManejoPasantia();
        modoModificar = true;

        pasantia = pasantiaController.findEntity(id);

        return "manejoPasantia";
    }

    private void inicializarManejoPasantia() {
        pasantia = new Pasantia();
        GestorContrato.getInstance().actualizarListaContrato();
    }

    public String initCrearPasantia() {
        inicializarManejoPasantia();
        modoModificar = false;

        return "manejoPasantia";
    }

    public void actualizarListado() {
        try {
            listaPasantias = pasantiaController.listPasantias();
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static GestorPersona getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPersona}", GestorPersona.class);
        return (GestorPersona) ex.getValue(context);
    }

    public String guardar() {
        try {
            if (modoModificar) {
                pasantiaController.edit(pasantia);
            } else {
                pasantiaController.create(pasantia);
            }
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorPasantia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public Pasantia getPasantia() {
        return pasantia;
    }

    public void setPersona(Pasantia pasantia) {
        this.pasantia = pasantia;
    }

    public List<Pasantia> getListaPasantias() {
        return listaPasantias;
    }

    public void setListaPasantias(List<Pasantia> listaPasantias) {
        this.listaPasantias = listaPasantias;
    }

}
