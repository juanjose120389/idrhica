/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.util;

import org.primefaces.context.RequestContext;

/**
 *
 * @author marcelocaj
 */
public class BeanUtils {
    public static void ejecutarJS(String codigo) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute(codigo);
    }
    
}
