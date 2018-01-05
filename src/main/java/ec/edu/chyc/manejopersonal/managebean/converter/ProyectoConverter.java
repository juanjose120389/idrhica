/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

//import ec.edu.chyc.manejopersonal.managebean.GestorContratado;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.managebean.GestorProyecto;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("proyectoConverter")
public class ProyectoConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Proyecto> list = GestorProyecto.getInstance().getListaProyecto();

       // List<Contratado> list = GestorContratado.getInstance().getListaContratados();
        for (Proyecto pro : list) {
            if (pro.getId()== id) 
                return pro;
        }
        Proyecto obj = new Proyecto();
        obj.setId(id);

        return obj;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Proyecto obj = (Proyecto) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   