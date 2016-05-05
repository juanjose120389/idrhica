/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.entity.Titulo;
import ec.edu.chyc.manejopersonal.managebean.GestorGeneral;
import ec.edu.chyc.manejopersonal.managebean.GestorInstitucion;
import ec.edu.chyc.manejopersonal.managebean.GestorPersona;
import ec.edu.chyc.manejopersonal.managebean.GestorProyecto;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("institucionConverter")
public class InstitucionConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Institucion> list = GestorInstitucion.getInstance().getListaInstituciones();
        for (Institucion inst : list) {
            if (inst.getId() == id) 
                return inst;
        }        
        for (Institucion inst : GestorProyecto.getInstance().getListaInstitucionesAgregadas()) {
            if (inst.getId() == id) 
                return inst;            
        }
        
        return null;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Institucion obj = (Institucion) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   