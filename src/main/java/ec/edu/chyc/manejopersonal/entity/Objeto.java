/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author CHYC-DK-005
 */
@Entity
public class Objeto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_interno")
    private String idInterno;

    private String serie;

    private String descripcion;

    private String ubicacion;

    private String marca;

    private String modelo;

    private String dimensiones;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "fecha_adquirido")
    private Date fechaAdquirido;

    private Double valor1;

    private Double valor2;

    private Double depreciacion;

    @Column(name = "valor_libros")
    private Double valorLibros;

    @Column(name = "vida_util")
    private String vidaUtil;

    @Column(name = "nombre_cta_contable")
    private String nombreCtaContable;

    @Column(name = "numero_cta_contable")
    private String numeroCtaContable;

    @Column(name = "proceso_compra")
    private String procesoCompra;

    @Column(name = "num_ped_proveeduria")
    private String numeroPedProveeduria;

    @Column(name = "numero_factura")
    private String numeroFactura;

    private String observacion;

    @Column(name = "ubicacion_seguro")
    private String ubicacionSeguro;

    private String garantia;

    @ManyToOne
    @JoinColumn(name = "id_custodio", referencedColumnName = "id")
    private Persona custodio;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", referencedColumnName = "id")
    private Proveedor proveedor;

    @ManyToMany(mappedBy = "objetosCollection")
    private Set<CategoriaObjeto> categoriasObjeto = new HashSet<>();

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the idInterno
     */
    public String getIdInterno() {
        return idInterno;
    }

    /**
     * @param idInterno the idInterno to set
     */
    public void setIdInterno(String idInterno) {
        this.idInterno = idInterno;
    }

    /**
     * @return the serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the ubicacion
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * @return the marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * @return the modelo
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * @return the dimensiones
     */
    public String getDimensiones() {
        return dimensiones;
    }

    /**
     * @param dimensiones the dimensiones to set
     */
    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    /**
     * @return the fechaAdquirido
     */
    public Date getFechaAdquirido() {
        return fechaAdquirido;
    }

    /**
     * @param fechaAdquirido the fechaAdquirido to set
     */
    public void setFechaAdquirido(Date fechaAdquirido) {
        this.fechaAdquirido = fechaAdquirido;
    }

    /**
     * @return the valor1
     */
    public Double getValor1() {
        return valor1;
    }

    /**
     * @param valor1 the valor1 to set
     */
    public void setValor1(Double valor1) {
        this.valor1 = valor1;
    }

    /**
     * @return the valor2
     */
    public Double getValor2() {
        return valor2;
    }

    /**
     * @param valor2 the valor2 to set
     */
    public void setValor2(Double valor2) {
        this.valor2 = valor2;
    }

    /**
     * @return the depreciacion
     */
    public Double getDepreciacion() {
        return depreciacion;
    }

    /**
     * @param depreciacion the depreciacion to set
     */
    public void setDepreciacion(Double depreciacion) {
        this.depreciacion = depreciacion;
    }

    /**
     * @return the valorLibros
     */
    public Double getValorLibros() {
        return valorLibros;
    }

    /**
     * @param valorLibros the valorLibros to set
     */
    public void setValorLibros(Double valorLibros) {
        this.valorLibros = valorLibros;
    }

    /**
     * @return the vidaUtil
     */
    public String getVidaUtil() {
        return vidaUtil;
    }

    /**
     * @param vidaUtil the vidaUtil to set
     */
    public void setVidaUtil(String vidaUtil) {
        this.vidaUtil = vidaUtil;
    }

    /**
     * @return the nombreCtaContable
     */
    public String getNombreCtaContable() {
        return nombreCtaContable;
    }

    /**
     * @param nombreCtaContable the nombreCtaContable to set
     */
    public void setNombreCtaContable(String nombreCtaContable) {
        this.nombreCtaContable = nombreCtaContable;
    }

    /**
     * @return the numeroCtaContable
     */
    public String getNumeroCtaContable() {
        return numeroCtaContable;
    }

    /**
     * @param numeroCtaContable the numeroCtaContable to set
     */
    public void setNumeroCtaContable(String numeroCtaContable) {
        this.numeroCtaContable = numeroCtaContable;
    }

    /**
     * @return the procesoCompra
     */
    public String getProcesoCompra() {
        return procesoCompra;
    }

    /**
     * @param procesoCompra the procesoCompra to set
     */
    public void setProcesoCompra(String procesoCompra) {
        this.procesoCompra = procesoCompra;
    }

    /**
     * @return the numeroPedProveeduria
     */
    public String getNumeroPedProveeduria() {
        return numeroPedProveeduria;
    }

    /**
     * @param numeroPedProveeduria the numeroPedProveeduria to set
     */
    public void setNumeroPedProveeduria(String numeroPedProveeduria) {
        this.numeroPedProveeduria = numeroPedProveeduria;
    }

    /**
     * @return the numeroFactura
     */
    public String getNumeroFactura() {
        return numeroFactura;
    }

    /**
     * @param numeroFactura the numeroFactura to set
     */
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /**
     * @return the ubicacionSeguro
     */
    public String getUbicacionSeguro() {
        return ubicacionSeguro;
    }

    /**
     * @param ubicacionSeguro the ubicacionSeguro to set
     */
    public void setUbicacionSeguro(String ubicacionSeguro) {
        this.ubicacionSeguro = ubicacionSeguro;
    }

    /**
     * @return the garantia
     */
    public String getGarantia() {
        return garantia;
    }

    /**
     * @param garantia the garantia to set
     */
    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    /**
     * @return the custodio
     */
    public Persona getCustodio() {
        return custodio;
    }

    /**
     * @param custodio the custodio to set
     */
    public void setCustodio(Persona custodio) {
        this.custodio = custodio;
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

    /**
     * @return the categoriasObjeto
     */
    public Set<CategoriaObjeto> getCategoriasObjeto() {
        return categoriasObjeto;
    }

    /**
     * @param categoriasObjeto the categoriasObjeto to set
     */
    public void setCategoriasObjeto(Set<CategoriaObjeto> categoriasObjeto) {
        this.categoriasObjeto = categoriasObjeto;
    }

}
