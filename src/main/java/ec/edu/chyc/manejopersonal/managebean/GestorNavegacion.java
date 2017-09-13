/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author CHYC-DK-005 Clase para guardar estados y controlar navegación entre
 * paginas
 */
@Named(value = "gestorNavegacion")
@ApplicationScoped
public class GestorNavegacion implements Serializable {

    static final int ARTICULO = 1;
    static final int CONTRATO = 2;
    static final int CONVENIO = 3;
    static final int INSTITUCION = 4;
    static final int PASANTIA = 5;
    static final int PERSONA = 6;
    static final int PROYECTO = 7;
    static final int TESIS = 8;

    private List<EstadoPagina> listaNavegacion = new ArrayList<>();

    public GestorNavegacion() {
        EstadoPagina ep = new EstadoPagina();
        ep.setId(null);
        ep.setIdPagina(6);
        listaNavegacion.add(ep);
    }

    public void cargarPagina(int idPagina, Long idElemento) {

        System.err.println("&&%%%%%%%%%%&%&%&%&%&&&&&&&&&&&&&&&&&&&&&&&&&&&&########¬¬ :" + "idpagina:" + idPagina + ", idElemento:" + idElemento);

        String redirect_url = null;
        if (idElemento == null) {
            vaciarListaNavegacion();
            EstadoPagina ep = new EstadoPagina();
            ep.setIdPagina(idPagina);
            ep.setId(null);
            listaNavegacion.add(ep);
            //return 
            redirect_url = cargarPaginaLista(idPagina)+".jsf";

        } else {
            EstadoPagina ep = new EstadoPagina();
            ep.setIdPagina(idPagina);
            ep.setId(idElemento);
            listaNavegacion.add(ep);
            //return 
            redirect_url = cargarPaginaDetalle(idPagina, idElemento)+".jsf";
        }
        
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirect_url);
        } catch (IOException ex) {
            Logger.getLogger(GestorNavegacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String regresarPagina(int idPagina, Long idElemento) {
        if (idElemento == null) {
            return cargarPaginaLista(idPagina);
        } else {
            return cargarPaginaDetalle(idPagina, idElemento);
        }
    }

    private String cargarPaginaLista(int idPagina) {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex;

        switch (idPagina) {
            case ARTICULO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorArticulo}", GestorArticulo.class);
                GestorArticulo gestor = (GestorArticulo) ex.getValue(context);
                return gestor.initListarArticulos();
            }
            case CONTRATO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorContrato}", GestorContrato.class);
                GestorContrato gestor = (GestorContrato) ex.getValue(context);
                return gestor.initListarContratos();
            }
            case CONVENIO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorConvenio}", GestorConvenio.class);
                GestorConvenio gestor = (GestorConvenio) ex.getValue(context);
                return gestor.initListarConvenios();
            }
            case INSTITUCION: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorInstitucion}", GestorInstitucion.class);
                GestorInstitucion gestor = (GestorInstitucion) ex.getValue(context);
                return gestor.initListarInstituciones();
            }
            case PASANTIA: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPasantia}", GestorPasantia.class);
                GestorPasantia gestor = (GestorPasantia) ex.getValue(context);
                return gestor.initListarPasantias();
            }
            case PERSONA: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorGeneral}", GestorGeneral.class);
                GestorGeneral gestor = (GestorGeneral) ex.getValue(context);
                return gestor.regresarPaginaPrincipal();
            }
            case PROYECTO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorProyecto}", GestorProyecto.class);
                GestorProyecto gestor = (GestorProyecto) ex.getValue(context);
                return gestor.initListarProyectos();
            }
            case TESIS: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorTesis}", GestorTesis.class);
                GestorTesis gestor = (GestorTesis) ex.getValue(context);
                return gestor.initListarTesis();
            }
            default:
                return "";
        }
    }

    private String cargarPaginaDetalle(int idPagina, Long idElemento) {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex;

        switch (idPagina) {
            case ARTICULO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorArticulo}", GestorArticulo.class);
                GestorArticulo gestor = (GestorArticulo) ex.getValue(context);
                return gestor.initVerArticulo(idElemento);
            }
            case CONTRATO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorContrato}", GestorContrato.class);
                GestorContrato gestor = (GestorContrato) ex.getValue(context);
                //gestor.init;
                break;
            }
            case CONVENIO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorConvenio}", GestorConvenio.class);
                GestorConvenio gestor = (GestorConvenio) ex.getValue(context);
                return gestor.initVerConvenio(idElemento);
            }
            case INSTITUCION: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorInstitucion}", GestorInstitucion.class);
                GestorInstitucion gestor = (GestorInstitucion) ex.getValue(context);
                return gestor.initVerInstitucion(idElemento);
            }
            case PASANTIA: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPasantia}", GestorPasantia.class);
                GestorPasantia gestor = (GestorPasantia) ex.getValue(context);
                return gestor.initVerPasantia(idElemento);
            }
            case PERSONA: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPersona}", GestorPersona.class);
                GestorPersona gestor = (GestorPersona) ex.getValue(context);
                return gestor.initVerPersona(idElemento);
            }
            case PROYECTO: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorProyecto}", GestorProyecto.class);
                GestorProyecto gestor = (GestorProyecto) ex.getValue(context);
                return gestor.initVerProyecto(idElemento);
            }
            case TESIS: {
                ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorTesis}", GestorTesis.class);
                GestorTesis gestor = (GestorTesis) ex.getValue(context);
                return gestor.initVerTesis(idElemento);
            }
            default:
                return "";
        }
        return "";
    }

    private void vaciarListaNavegacion() {
        listaNavegacion.clear();
        System.err.println("&&%%%%%%%%%%&%&%&%&%&&&&&&&&&&&&&&&&&&&&&&&&&&&&########¬¬ :" + "Limpia la lista de navegacion");   
    }

    public void regresar() {
        int size = listaNavegacion.size();
        System.err.println("&&%%%%%%%%%%&%&%&%&%&&&&&&&&&&&&&&&&&&&&&&&&&&&&########¬¬ :" + size);
        listaNavegacion.remove(listaNavegacion.size() - 1);
        EstadoPagina ep = listaNavegacion.get(listaNavegacion.size() - 1);
        //return 
        String redirect_url=regresarPagina(ep.getIdPagina(), ep.getId())+".jsf";
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirect_url);
        } catch (IOException ex) {
            Logger.getLogger(GestorNavegacion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

}
