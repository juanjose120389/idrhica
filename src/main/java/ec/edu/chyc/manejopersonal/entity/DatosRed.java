/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * @author CHYC-DK-005
 */
public class DatosRed implements Serializable{
    @Id
    Objeto objeto;
    
    String IP;
    
    @Column(name="nombre_red")
    String nombreRed;
    
    @Column(name="clave")
    String clave;
    
}
