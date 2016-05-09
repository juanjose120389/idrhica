/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ContratoJpaController;
import ec.edu.chyc.manejopersonal.entity.Contrato;
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
@Named(value = "gestorContrato")
@SessionScoped
public class GestorContrato implements Serializable {

    private final ContratoJpaController contratoController = new ContratoJpaController();

    private List<Contrato> listaContrato = new ArrayList<>();
    private Contrato contrato = new Contrato();
    private boolean modoModificar = false;

    private boolean esProfesor = false;

    public GestorContrato() {
    }

    @PostConstruct
    public void init() {
    }

    public static GestorContrato getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorContrato}", GestorContrato.class);
        return (GestorContrato) ex.getValue(context);
    }

    public void actualizarListaContrato() {
        try {
            listaContrato = contratoController.listContrato();
        } catch (Exception ex) {
            Logger.getLogger(GestorContrato.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializarManejoContrato() {
        contrato = new Contrato();
        esProfesor = false;
        GestorProyecto.getInstance().actualizarListaProyecto();
        GestorPersona.getInstance().actualizarListaPersonasConContrato();
    }
    
    public String initModificarContrato(Long id) {
        //contrato = contratoController.
        initializarManejoContrato();
        
        contrato = contratoController.findContrato(id);
        
        if (contrato.getTipoProfesor() != null) {
            esProfesor = true;
        }
        modoModificar = true;
        
        return "manejoContratos";
    }
    
    public String initCrearContrato() {
        initializarManejoContrato();
        modoModificar = false;
        //actualizarListaContrato();
        return "manejoContratos";
    }

    public String initListarContratos() {
        actualizarListaContrato();
        return "listaContratos";
    }

    public String guardar() {
        if (!esProfesor) {
            contrato.setTipoProfesor(null);
        }        
        try {
            if (modoModificar) {
                contratoController.edit(contrato);
            } else {
                contratoController.create(contrato);
            }
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorContrato.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public boolean puedeSerContratoProfesor() {
        return contrato.getTipo() == Contrato.TipoContrato.SERCOP;
    }

    public boolean isEsProfesor() {
        return esProfesor;
    }

    public void setEsProfesor(boolean esProfesor) {
        this.esProfesor = esProfesor;
    }

    public Contrato.TipoContrato[] getTiposContrato() {
        return Contrato.TipoContrato.values();
    }

    public Contrato.TipoProfesor[] getTiposProfesor() {
        return Contrato.TipoProfesor.values();
    }
    
    public List<Contrato> getListaContrato() {
        return listaContrato;
    }

    public void setListaContrato(List<Contrato> listaContrato) {
        this.listaContrato = listaContrato;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public boolean isModoModificar() {
        return modoModificar;
    }

    public void setModoModificar(boolean modoModificar) {
        this.modoModificar = modoModificar;
    }
    
}
