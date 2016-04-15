/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.Titulo;
import ec.edu.chyc.manejopersonal.entity.Universidad;
import ec.edu.chyc.manejopersonal.managebean.GestorGeneral;
import ec.edu.chyc.manejopersonal.managebean.GestorPersona;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("tituloConverter")
public class TituloConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Titulo> list = GestorGeneral.getInstance().getListaTitulos();
        for (Titulo titu : list) {
            if (titu.getId() == id) 
                return titu;
        }        
        for (Titulo titu : GestorPersona.getInstance().getListaTitulosAgregados()) {
            if (titu.getId() == id) 
                return titu;            
        }
        
        return null;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Titulo obj = (Titulo) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   