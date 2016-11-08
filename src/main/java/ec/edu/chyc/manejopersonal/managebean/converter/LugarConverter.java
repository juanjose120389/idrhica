/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.Lugar;
import ec.edu.chyc.manejopersonal.managebean.GestorProyecto;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("lugarConverter")
public class LugarConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Lugar> list = GestorProyecto.getInstance().getListaLugares();
        for (Lugar obj : list) {
            if (obj.getId() == id) 
                return obj;
        }        
        for (Lugar obj : GestorProyecto.getInstance().getListaLugaresAgregados()) {
            if (obj.getId() == id) 
                return obj;            
        }
        
        return null;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Lugar obj = (Lugar) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   