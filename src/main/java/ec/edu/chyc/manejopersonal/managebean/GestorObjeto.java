/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ObjetoJpaController;
import ec.edu.chyc.manejopersonal.controller.ProveedorJpaController;
import ec.edu.chyc.manejopersonal.entity.Objeto;
import ec.edu.chyc.manejopersonal.entity.Proveedor;
import static ec.edu.chyc.manejopersonal.managebean.util.BeansUtils.ejecutarJS;
import ec.edu.chyc.manejopersonal.util.FechaUtils;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author CHYC-DK-005
 */
@Named(value = "gestorObjeto")
@SessionScoped
public class GestorObjeto implements Serializable {

    private Modo modo;
    private final ObjetoJpaController objetoController=new ObjetoJpaController();
    private final ProveedorJpaController proveedorController=new ProveedorJpaController();
    
    private List<Objeto> listaObjetos = new ArrayList<>();

    
    private List<Proveedor> listaProveedores=new ArrayList();
    private List<Proveedor> listaProveedoresAgregados=new ArrayList();
    
    private Objeto objeto;
    private Proveedor proveedor;
    
    
    private Long idProveedorGen = -1L;
    
    private boolean modoModificar = false;


    /**
     * @return the objeto
     */
    public Objeto getObjeto() {
        return objeto;
    }

    /**
     * @param objeto the objeto to set
     */
    public void setObjeto(Objeto objeto) {
        this.objeto = objeto;
    }

    /**
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<Objeto> getListaObjetos() {
        return listaObjetos;
    }

    public void setListaObjetos(List<Objeto> listaObjetos) {
        this.listaObjetos = listaObjetos;
    }
    
    private enum Modo {
        AGREGAR,
        EDITAR,
        VER
    }
    
    public GestorObjeto() {
        //TODO Borrar para que se pueda acceder desde el menu
       initCrearObjeto();
    }

    @PostConstruct
    public void init() {        
        proveedor = new Proveedor();
        objeto = new Objeto();
    }

    public String initCrearObjeto() {
        inicializarManejoObjeto();
        return "manejoObjetos";
    }

    public void inicializarManejoObjeto() {
        objeto=new Objeto();
        listaProveedoresAgregados.clear();
        actualizarListaProveedores();
    }

    public void abrirUbicacionDialog() {
        //mostrarDlgUbicacion = true;        
        ejecutarJS("PF('dlgUbicacion').show()");
        System.err.println("///////////////////////////////Abriendo dlgUbicacion");
    }

    public void abrirCustodioDialog() {
        //mostrarDlgCustodio = true;
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        ejecutarJS("PF('dlgCustodio').show()");
    }

    public void abrirProveedorDialog() {
        //mostrarDlgProveedor = true;
        //RequestContext.getCurrentInstance().update("divDialogs");
        ejecutarJS("PF('dlgProveedor').show()");
        System.err.println("///////////////////////////////Abriendo dlgProveedor");
    }

    public void guardarProveedor() {
        //objeto.setProveedor(getProveedor());
        proveedor.setId(idProveedorGen);
        idProveedorGen--;
        listaProveedoresAgregados.add(proveedor);
        objeto.setProveedor(proveedor);
        
        proveedor=new Proveedor();
        ejecutarJS("PF('dlgProveedor').hide()");
        //System.err.println("$$$$$$$$$$$**********" + proveedor);
    }
    
    public void guardarUbicacion() {
        //objeto.setProveedor(getProveedor());
        //proveedor.setId(idProveedorGen);
        //idProveedorGen--;
        //listaProveedoresAgregados.add(proveedor);
        //objeto.setProveedor(proveedor);
        
        //proveedor=new Proveedor();
        ejecutarJS("PF('dlgUbicacion').hide()");
        //System.err.println("$$$$$$$$$$$**********" + proveedor);
    }

    public boolean isModoModificar() {
        return modo == Modo.EDITAR;
    }
    
    public void actualizarListaProveedores() {
        try {
            listaProveedores = proveedorController.listProveedores();
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public List<Proveedor> getListaProveedoresAgregados() {
        return listaProveedoresAgregados;
    }
    
    public void actualizarListaObjetos() {
        try {
            listaObjetos = objetoController.listObjetos();
        } catch (Exception ex) {
            Logger.getLogger(GestorObjeto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String initListarObjetos(){
        actualizarListaObjetos();
        return "listaObjetos";
    }
    
    public String guardar(){
        try {
            if (modoModificar) {
                objetoController.edit(objeto);
            } else {
                objetoController.create(objeto);
            }            
            //GestorInstitucion.getInstance().getListaInstitucionesAgregadas().clear();
            return initListarObjetos();
            //return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorObjeto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static GestorObjeto getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorObjeto}", GestorObjeto.class);
        return (GestorObjeto) ex.getValue(context);
    }

}
