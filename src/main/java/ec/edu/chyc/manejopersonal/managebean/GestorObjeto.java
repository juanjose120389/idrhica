/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Objeto;
import ec.edu.chyc.manejopersonal.entity.Proveedor;
import static ec.edu.chyc.manejopersonal.managebean.util.BeansUtils.ejecutarJS;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
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
    /*private boolean mostrarDlgUbicacion;
    private boolean mostrarDlgProveedor;
    private boolean mostrarDlgCustodio;*/

    private Objeto objeto;
    private Proveedor proveedor;

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

    private enum Modo {
        AGREGAR,
        EDITAR,
        VER
    }

    public GestorObjeto() {
    }

    @PostConstruct
    public void init() {
        //actualizarListado();
        /*try {
            listaPersonas = personaController.listPersonas();
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        //universidad = new Universidad();
        //titulo = new Titulo();
        //tabActivo = 0;

        proveedor = new Proveedor();
        objeto = new Objeto();
    }

    public String initCrearObjeto() {
        inicializarManejoObjeto();

        //modoModificar = false;
        //modo = Modo.AGREGAR;
        /*LocalDate dtFechaNacDefault = LocalDateTime.now().toLocalDate();
        dtFechaNacDefault = dtFechaNacDefault.minusYears(28).withDayOfYear(1);
        
        persona.setFechaNacimiento(FechaUtils.asDate(dtFechaNacDefault));        */
        //persona.setFechaNacimiento(null);
        return "manejoObjetos";
    }

    public void inicializarManejoObjeto() {
        /*mostrarDlgUbicacion = false;
        mostrarDlgCustodio = false;
        mostrarDlgProveedor = false;*/
    }

    public void abrirUbicacionDialog() {
        //mostrarDlgUbicacion = true;
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        ejecutarJS("PF('dlgUbicacion').show()");
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
        ejecutarJS("PF('dlgProveedor').hide()");
        //System.err.println("$$$$$$$$$$$**********" + proveedor);
    }

    /*
    public void onCloseDlgUbicacion() {
        //mostrarDlgUbicacion = false;
    }

    public void onCloseDlgCustodio() {
        //mostrarDlgCustodio = false;
    }

    public void onCloseDlgProveedor() {
        //mostrarDlgProveedor = false;
    }
    

    public void setMostrarDlgUbicacion(boolean mostrarDlgUbicacion) {
        //this.mostrarDlgUbicacion = mostrarDlgUbicacion;
    }

    public void setMostrarDlgCustodio(boolean mostrarDlgCustodio) {
        //this.mostrarDlgCustodio = mostrarDlgCustodio;
    }

    public void setMostrarDlgProveedor(boolean mostrarDlgProveedor) {
        //this.mostrarDlgProveedor = mostrarDlgProveedor;
    }
    */

    public boolean isModoModificar() {
        return modo == Modo.EDITAR;
    }

    /*
    public boolean isMostrarDlgUbicacion() {
        //return mostrarDlgUbicacion;
    }

    public boolean isMostrarDlgCustodio() {
        return mostrarDlgCustodio;
    }

    public boolean isMostrarDlgProveedor() {
        return mostrarDlgProveedor;
    }*/

}
