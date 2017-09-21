/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Persona;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author CHYC-DK-005
 */
@FacesConverter("custodioConverter")
public class CustodioConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            GestorPersona personas = (GestorPersona) fc.getExternalContext().getApplicationMap().get("gestorPersona");
            return personas.getListaPersonas().get(Integer.parseInt(value));
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object!=null){
            return String.valueOf(((Persona) object).getId());
        }
        return null;
    }

}
