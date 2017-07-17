/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author CHYC-DK-005 Clase para guardar estados y controlar navegación entre
 * paginas
 */
@Named(value = "gestorNavegacion")
@SessionScoped
public class GestorNavegacion implements Serializable {

    static final int VER_ARTICULO = 1;
    static final int VER_CONTRATO = 2;
    static final int VER_CONVENIO = 3;
    static final int VER_INSTITUCION = 4;
    static final int VER_PASANTIA = 5;
    static final int VER_PERSONA = 6;
    static final int VER_PROYECTO = 7;
    static final int VER_TESIS = 8;

    /*static final int VER_LISTA_ARTICULO=1;
    static final int VER_LISTA_CONTRATO=2;
    static final int VER_LISTA_CONVENIO=3;
    static final int VER_LISTA_INSTITUCION=4;
    static final int VER_LISTA_PASANTIA=5;
    static final int VER_LISTA_PERSONA=6;
    static final int VER_LISTA_PROYECTO=7;
    static final int VER_LISTA_TESIS=8;*/
    public void regresar(int idPagDestino, long id) {

        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex;

        if (idPagDestino == VER_PASANTIA) {
            ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPasantia}", GestorPasantia.class);
            GestorPasantia gestor = (GestorPasantia) ex.getValue(context);
        }
    }

}
