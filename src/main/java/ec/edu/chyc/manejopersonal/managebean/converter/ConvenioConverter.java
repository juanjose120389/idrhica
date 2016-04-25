/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.Convenio;
import ec.edu.chyc.manejopersonal.managebean.GestorConvenio;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("convenioConverter")
public class ConvenioConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        Long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Convenio> list = GestorConvenio.getInstance().getListaConvenios();
        for (Convenio con : list) {
            if (con.getId().equals(id)) 
                return con;
        }
        return null;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        Convenio obj = (Convenio) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   