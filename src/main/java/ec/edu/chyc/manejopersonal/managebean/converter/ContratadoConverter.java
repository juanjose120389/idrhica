/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

//import ec.edu.chyc.manejopersonal.managebean.GestorContratado;
import ec.edu.chyc.manejopersonal.entity.Contratado;
import ec.edu.chyc.manejopersonal.managebean.GestorContratado;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("contratadoConverter")
public class ContratadoConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        Long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Contratado> list = GestorContratado.getInstance().getListaContratados();
       // List<Contratado> list = GestorContratado.getInstance().getListaContratados();
        for (Contratado con : list) {
            if (con.getId() == id) 
                return con;
        }
        Contratado obj = new Contratado();
        obj.setId(id);

        return obj;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Contratado obj = (Contratado) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   