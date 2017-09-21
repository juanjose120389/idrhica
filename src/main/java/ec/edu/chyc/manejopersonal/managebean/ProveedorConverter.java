/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Proveedor;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author CHYC-DK-005
 */
@FacesConverter("proveedorConverter")
public class ProveedorConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        String v=value;
        System.err.println("€¬€¬€¬¬#@#~€~#@€~#@VALUE: "+value);
        if (value != null && value.trim().length() > 0) {
            try {
                GestorProveedor proveedores = (GestorProveedor) fc.getExternalContext().getApplicationMap().get("gestorProveedor");
                //return proveedores.getListaProveedores().get(Integer.parseInt(value));
                return proveedores.getListaProveedores().get(0);
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid proveedor."));
            }
        } else {
            //return null;
            GestorProveedor proveedores = (GestorProveedor) fc.getExternalContext().getApplicationMap().get("gestorProveedor");
            //return proveedores.getListaProveedores().get(Integer.parseInt(value));
            return proveedores.getListaProveedores().get(0);

        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        Proveedor p=(Proveedor) object;
        if (object != null) {
            //return String.valueOf(((Proveedor) object).getId());
            return "1234";
        } else {
            //return null;
            return "1234";
        }
    }

}
