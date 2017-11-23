/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.converter;

import ec.edu.chyc.manejopersonal.entity.Proveedor;
import ec.edu.chyc.manejopersonal.managebean.GestorObjeto;
import ec.edu.chyc.manejopersonal.managebean.GestorProveedor;
import java.util.List;
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

        System.err.println("############ Valor recuperado (dlg Proveedor): " + RUCProveedor);
        if (RUCProveedor != null && RUCProveedor.trim().length() > 0) {
            
            List<Proveedor> list = GestorObjeto.getInstance().getListaProveedores();
            for (Proveedor obj : list) {
                if (obj.getRuc().equals(RUCProveedor)) {
                    return obj;
                }
            }
            for (Proveedor obj : GestorObjeto.getInstance().getListaProveedoresAgregados()) {
                if (obj.getRuc().equals(RUCProveedor)) {
                    return obj;
                }
            }
            return null;
        } else {
            Proveedor provN = new Proveedor();
            provN.setRuc("Seleccionar...");
            return provN;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object proveedor) {
        if (proveedor != null && proveedor != "") {
            Proveedor p = (Proveedor) proveedor;
            return String.valueOf(((Proveedor) proveedor).getRuc());
        } else {
            return null;
        }
    }

}
