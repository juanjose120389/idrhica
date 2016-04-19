/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.managebean.GestorPersona;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("personaConverter")
public class PersonaConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        Long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Persona> list = GestorPersona.getInstance().getListaPersonas();
       // List<Contratado> list = GestorContratado.getInstance().getListaContratados();
        for (Persona per : list) {
            if (per.getId() == id) 
                return per;
        }
        Persona obj = new Persona();
        obj.setId(id);

        return obj;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Persona obj = (Persona) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   