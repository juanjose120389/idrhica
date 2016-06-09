/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.Universidad;
import ec.edu.chyc.manejopersonal.managebean.GestorGeneral;
import ec.edu.chyc.manejopersonal.managebean.GestorPersona;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("universidadConverter")
public class UniversidadConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Universidad> list = GestorGeneral.getInstance().getListaUniversidades();
        for (Universidad uni : list) {
            if (uni.getId() == id) 
                return uni;
        }
        for (Universidad uni : GestorGeneral.getInstance().getListaUniversidadesAgregadas()) {
            if (uni.getId() == id) 
                return uni;            
        }        /*
        for (Universidad uni : GestorPersona.getInstance().getListaUniversidadesAgregadas()) {
            if (uni.getId() == id) 
                return uni;            
        }*/
        
        Universidad obj = new Universidad();
        obj.setId(id);

        return obj;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Universidad obj = (Universidad) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   