/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Proveedor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author CHYC-DK-005
 */
@Named(value = "gestorProveedor")
@SessionScoped
public class GestorProveedor implements Serializable{
    
    private List<Proveedor> listaProveedores=new ArrayList<>();
    private Proveedor proveedor=new Proveedor();

    public GestorProveedor() {
        
    }
    
    @PostConstruct
    public void init(){
        llenarListaProveedores();
    }
    
    public void llenarListaProveedores(){
        Proveedor p1=new Proveedor(1L, "01", "prov1", "prov 1 desc");
        Proveedor p2=new Proveedor(2L, "02", "prov2", "prov 2 desc");
        Proveedor p3=new Proveedor(3L, "03", "prov3", "prov 3 desc");
        Proveedor p4=new Proveedor(4L, "04", "prov4", "prov 4 desc");
        Proveedor p5=new Proveedor(5L, "05", "prov5", "prov 5 desc");
        
        listaProveedores.add(p1);
        listaProveedores.add(p2);
        listaProveedores.add(p3);
        listaProveedores.add(p4);
        listaProveedores.add(p5);
    }

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    
    public Proveedor buscarProveedor(String RUC){
        for (Proveedor proveedor : listaProveedores) {
            if (proveedor.getRuc().equalsIgnoreCase(RUC)) {
                return proveedor;
            }
        }
        return null;
    }
    
    
    
    
    
}
