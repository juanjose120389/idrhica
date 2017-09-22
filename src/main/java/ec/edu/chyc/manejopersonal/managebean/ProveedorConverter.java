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
    public Object getAsObject(FacesContext fc, UIComponent uic, String RUCProveedor) {
      
        System.err.println("############ Valor recuperado (dlg Proveedor): "+RUCProveedor);
        if(RUCProveedor != null && RUCProveedor.trim().length() > 0) {
            try {
                GestorProveedor gestorProveedor = (GestorProveedor) fc.getExternalContext().getApplicationMap().get("gestorProveedor");
                return gestorProveedor.buscarProveedor(RUCProveedor);
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object proveedor) {
        Proveedor p=(Proveedor) proveedor;
        if(proveedor != null) {
            return String.valueOf(((Proveedor) proveedor).getRuc());
        }
        else {
            return null;
        }
    }

}
