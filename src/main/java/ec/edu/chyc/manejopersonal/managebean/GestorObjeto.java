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
 * @author CHYC-DK-005
 */
@Named(value = "gestorObjeto")
@SessionScoped
public class GestorObjeto implements Serializable{
    
    public String initCrearObjeto() {
        //inicializarManejoPersona();
        
        //modoModificar = false;
        //modo = Modo.AGREGAR;
        
        /*LocalDate dtFechaNacDefault = LocalDateTime.now().toLocalDate();
        dtFechaNacDefault = dtFechaNacDefault.minusYears(28).withDayOfYear(1);
        
        persona.setFechaNacimiento(FechaUtils.asDate(dtFechaNacDefault));        */
        //persona.setFechaNacimiento(null);
        
        return "manejoObjetos";
    }
        
}
