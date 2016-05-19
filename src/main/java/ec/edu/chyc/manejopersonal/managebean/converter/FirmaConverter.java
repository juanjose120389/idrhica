/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.controller.PersonaJpaController;
import ec.edu.chyc.manejopersonal.entity.Firma;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("firmaConverter")
public class FirmaConverter implements Converter {
    private final PersonaJpaController personaController = new PersonaJpaController();
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
        Firma firma = personaController.findFirma(id);

        return firma;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Firma obj = (Firma) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   