/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ConvenioJpaController;
import ec.edu.chyc.manejopersonal.entity.Convenio;
import ec.edu.chyc.manejopersonal.util.FechaUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorConvenio")
@SessionScoped
public class GestorConvenio implements Serializable {
    private final ConvenioJpaController convenioController = new ConvenioJpaController();
    private Convenio convenio;
    private List<Convenio> listaConvenios = new ArrayList<>();
    private boolean modoModificar;
    
    public GestorConvenio() {
    }
    @PostConstruct
    public void init() {
        
    }
    
    public String initCrearConvenio() {
        convenio = new Convenio();
        modoModificar = false;
        GestorProyecto.getInstance().actualizarListaProyecto();
        GestorInstitucion.getInstance().actualizarListaInstituciones();
        
        return "manejoConvenio";
    }
    public void actualizarListaConvenios() {
        try {
            listaConvenios = convenioController.listConvenio();
        } catch (Exception ex) {
            Logger.getLogger(GestorConvenio.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    public String initListarConvenios() {
        listaConvenios.clear();
        actualizarListaConvenios();
        modoModificar = false;
        return "listaConvenios";
    }
    public String initModificarConvenio(Long id) {
        convenio = convenioController.findEntity(id);
        modoModificar = true;
        GestorProyecto.getInstance().actualizarListaProyecto();
        
        return "manejoConvenio";
    }
    public static GestorConvenio getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorConvenio}",GestorConvenio.class);
        return (GestorConvenio)ex.getValue(context);
    }    
    
    public String guardar() {
        LocalDate dtInicio = FechaUtils.asLocalDate(convenio.getFechaInicio());
        LocalDate dtFin = FechaUtils.asLocalDate(convenio.getFechaFin());
        //MutableDateTime dtInicio = new MutableDateTime(convenio.getFechaInicio());
        //MutableDateTime dtFin = new MutableDateTime(convenio.getFechaFin());
        
        if (dtFin.isBefore(dtInicio)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha de finalización debe ser mayor a la de inicio.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }
        
        try {
            if (modoModificar) {
                convenioController.edit(convenio);
            } else {
                convenioController.create(convenio);
            }
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorConvenio.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public List<Convenio> getListaConvenios() {
        return listaConvenios;
    }

    public void setListaConvenios(List<Convenio> listaConvenios) {
        this.listaConvenios = listaConvenios;
    }

    public boolean isModoModificar() {
        return modoModificar;
    }

    public void setModoModificar(boolean modoModificar) {
        this.modoModificar = modoModificar;
    }
    
}
