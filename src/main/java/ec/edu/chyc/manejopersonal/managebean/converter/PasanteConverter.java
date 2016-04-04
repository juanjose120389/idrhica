/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

//import ec.edu.chyc.manejopersonal.managebean.GestorContratado;
import ec.edu.chyc.manejopersonal.entity.Pasante;
import ec.edu.chyc.manejopersonal.managebean.GestorPasante;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("pasanteConverter")
public class PasanteConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        Long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Pasante> list = GestorPasante.getInstance().getListaPasantes();
       // List<Contratado> list = GestorContratado.getInstance().getListaContratados();
        for (Pasante pas : list) {
            if (pas.getId() == id) 
                return pas;
        }
        Pasante obj = new Pasante();
        obj.setId(id);

        return obj;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Pasante obj = (Pasante) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   