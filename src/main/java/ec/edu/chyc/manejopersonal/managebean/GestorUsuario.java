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
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static GestorUsuario getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorUsuario}", GestorUsuario.class);
        return (GestorUsuario) ex.getValue(context);
    }
    public boolean isPuedeAgregar() {
        return (logeado && usuarioActual.getTipo() == TipoUsuario.ADMIN);
    }
    public boolean isPuedeEditar() {
        return (logeado && usuarioActual.getTipo() == TipoUsuario.ADMIN);
    }
    public boolean isPuedeVerMasInfo(){
        if (logeado) {
            return (usuarioActual.getTipo() == TipoUsuario.ADMIN || usuarioActual.getTipo() == TipoUsuario.VISOR);
        }
        return false;
    }
    
    public void pollComplete() {
        //BeansUtils.ejecutarJS("PF('pollEnable').stop();");
        bloquearLogin = false;
    }
    
    public String login() {
        if (!usuario.isEmpty() && !password.isEmpty() && !bloquearLogin) {
            String encPassword = ServerUtils.sha256(password);

            Usuario user = usuarioController.login(usuario, encPassword);
            if (user != null) {
                usuarioActual = user;
                logeado = true;
                intentosIncorrectos = 0;
            } else {
                intentosIncorrectos++;
                if (intentosIncorrectos >= 3) {
                    bloquearLogin = true;
                    BeansUtils.ejecutarJS("PF('pollEnable').start();");                    
                }
            }
        }
        return "";
    }
    public String logout() {
        usuarioActual = null;
        logeado = false;
        usuario = "";
        password = "";
        intentosIncorrectos = 0;
        return "";
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
