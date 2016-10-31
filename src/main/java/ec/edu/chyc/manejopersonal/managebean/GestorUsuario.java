/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.UsuarioJpaController;
import ec.edu.chyc.manejopersonal.entity.Usuario;
import ec.edu.chyc.manejopersonal.entity.Usuario.TipoUsuario;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorUsuario")
@SessionScoped
public class GestorUsuario implements Serializable {
    private final UsuarioJpaController usuarioController = new UsuarioJpaController();
    private String usuario = "";
    private String password = "";
    private boolean logeado = false;
    private Usuario usuarioActual = null;
    private int intentosIncorrectos = 0;
    private boolean bloquearLogin = false;

    public static GestorUsuario getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorUsuario}", GestorUsuario.class);
        return (GestorUsuario) ex.getValue(context);
    }
    /**
     * 
     * @return true Si el usuario puede agregar datos
     */
    public boolean isPuedeAgregar() {
        return (logeado && usuarioActual.getTipo() == TipoUsuario.ADMIN);
    }
    /**
     * 
     * @return true Si el usuario puede editar datos
     */
    public boolean isPuedeEditar() {
        return (logeado && usuarioActual.getTipo() == TipoUsuario.ADMIN);
    }
    /**
     * 
     * @return true Si el usuario puede ver información privilegiada
     */
    public boolean isPuedeVerMasInfo(){
        if (logeado) {
            return (usuarioActual.getTipo() == TipoUsuario.ADMIN || usuarioActual.getTipo() == TipoUsuario.VISOR);
        }
        return false;
    }
    
    /**
     * Cuando el tiempo de bloqueo de login se cumplió, desbloquear login
     */
    public void pollComplete() {
        //BeansUtils.ejecutarJS("PF('pollEnable').stop();");
        bloquearLogin = false;
    }
    
    public String login() {
        if (!usuario.isEmpty() && !password.isEmpty()) {
            //antes de enviar el password, se lo codifica ya que en la base de datos está el password codificado
            String encPassword = ServerUtils.sha256(password);

            Usuario user = usuarioController.login(usuario, encPassword);
            if (user != null) {
                usuarioActual = user;
                logeado = true;
                intentosIncorrectos = 0;
            } else {
                intentosIncorrectos++;
                if (intentosIncorrectos >= 3) {//3 o más intentos incorrectos, bloquear login
                    bloquearLogin = true;
                    BeansUtils.ejecutarJS("PF('pollEnable').start();");                    
                }
            }
        }
        return "";
    }

    public String logout() {
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();

        //todas las páginas manejo deben ser accesibles solo cuando se inicia sesión, 
        // por lo tanto si se encuentra en esas páginas, redireccionar a home
        boolean redirHome = false;
        if (viewId.startsWith("/manejo")) {
            redirHome = true;
        }
        
        usuarioActual = null;
        logeado = false;
        usuario = "";
        password = "";
        intentosIncorrectos = 0;

        if (redirHome) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("./");
            } catch (IOException ex) {
                Logger.getLogger(GestorUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return redirHome ? "index" : "";
    }
    
    public GestorUsuario() {
    }

    public boolean isLogeado() {
        return logeado;
    }

    public void setLogeado(boolean logeado) {
        this.logeado = logeado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public boolean isBloquearLogin() {
        return bloquearLogin;
    }

    public void setBloquearLogin(boolean bloquearLogin) {
        this.bloquearLogin = bloquearLogin;
    }    
}
