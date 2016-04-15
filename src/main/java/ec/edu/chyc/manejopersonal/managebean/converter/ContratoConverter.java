/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

//import ec.edu.chyc.manejopersonal.managebean.GestorContratado;
import ec.edu.chyc.manejopersonal.entity.Contrato;
import ec.edu.chyc.manejopersonal.managebean.GestorContrato;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("contratoConverter")
public class ContratoConverter implements Converter {
 
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        Long id;
        try {
            id = Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }

        List<Contrato> list = GestorContrato.getInstance().getListaContrato();
       // List<Contratado> list = GestorContratado.getInstance().getListaContratados();
        for (Contrato con : list) {
            if (con.getId() == id) 
                return con;
        }
        Contrato obj = new Contrato();
        obj.setId(id);

        return obj;
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == "" || value == null) {
            return null;
        }
        // Convert ProjectDetail to its unique String representation.
        Contrato obj = (Contrato) value;
        String idAsString = String.valueOf(obj.getId());
        return idAsString;
    }   

}   