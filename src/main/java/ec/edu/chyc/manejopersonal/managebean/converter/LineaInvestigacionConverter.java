/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.LineaInvestigacion;
import ec.edu.chyc.manejopersonal.managebean.GestorProyecto;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("lineaInvestigacionConverter")
public class LineaInvestigacionConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<LineaInvestigacion> list = GestorProyecto.getInstance().getListaLineasInvestigacion();
        for (LineaInvestigacion obj : list) {
            if (obj.getId() == id) 
                return obj;
        }        
        for (LineaInvestigacion obj : GestorProyecto.getInstance().getListaLineasInvestigacionAgregados()) {
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
        LineaInvestigacion obj = (LineaInvestigacion) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   