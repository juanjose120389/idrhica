/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Contrato;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class ContratoJpaController extends GenericJpaController<Contrato> implements Serializable {
    public ContratoJpaController() {
        setClassRef(Contrato.class);
    }

    public List<Contrato> listContrato() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct p from Contrato p join fetch p.proyectosCollection");
            List<Contrato> list = q.getResultList();
            
            for (Contrato contrato : list) {
                if (contrato.getTipoProfesor() == null) {
                    //si no es profesor, llenar la variable contrato.proyecto con el unico proyecto relacionado
                    contrato.setProyecto(contrato.getProyectosCollection().stream().findFirst().get());
                }
            }
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    public Contrato findContrato(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select p from Contrato p join fetch p.proyectosCollection where p.id=:id");
            q.setParameter("id", id);
            Contrato contrato = (Contrato)q.getSingleResult();
            return contrato;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
        
    }
    
    public void create(Contrato obj) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(obj);
           
            for (Proyecto proyecto : obj.getProyectosCollection()) {
                Proyecto proyectoAttached = em.find(Proyecto.class, proyecto.getId());
                proyectoAttached.getContratosCollection().add(obj);
            }

            
            if (!obj.getArchivoContrato().isEmpty()) {
                //si se subió el archivo, copiar del directorio de temporales al original destino, después eliminar el archivo temporal
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoContrato());
                Path destino = ServerUtils.getPathContratos().resolve(obj.getArchivoContrato());

                Files.move(origen, destino, REPLACE_EXISTING);
            }
            
            
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Contrato obj) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            Contrato contratoAntiguo = em.find(Contrato.class, obj.getId());
            
            
            Set<Proyecto> listaProyectos = obj.getProyectosCollection();
            Iterator<Proyecto> iterProyectosAnterior = contratoAntiguo.getProyectosCollection().iterator();
            while (iterProyectosAnterior.hasNext()) {
                Proyecto proy = iterProyectosAnterior.next();
                if (!listaProyectos.contains(proy)) {
                    iterProyectosAnterior.remove();
                    proy.getContratosCollection().remove(contratoAntiguo);
                }
            }
            for (Proyecto proy : listaProyectos) {
                if (!contratoAntiguo.getProyectosCollection().contains(proy)) {
                    Proyecto proyExistenteAsignado = em.find(Proyecto.class, proy.getId());
                    proyExistenteAsignado.getContratosCollection().add(obj);
                }
            }
            
            
            
            
            String archivoAntiguo = contratoAntiguo.getArchivoContrato();
            String archivoNuevo = obj.getArchivoContrato();
            
            ServerUtils.procesarAntiguoNuevoArchivo(archivoAntiguo, archivoNuevo, ServerUtils.getPathContratos());
            
            em.merge(obj);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }
    
    public void destroy(Long id) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.remove(id);
            em.getTransaction().commit();
                    
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }    

}
