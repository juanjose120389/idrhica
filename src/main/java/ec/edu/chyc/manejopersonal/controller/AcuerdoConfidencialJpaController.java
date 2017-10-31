/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.AcuerdoConfidencial;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AcuerdoConfidencialJpaController
        extends GenericJpaController<AcuerdoConfidencial>
        implements Serializable {
    
    public AcuerdoConfidencialJpaController() {
        setClassRef(AcuerdoConfidencial.class);
    }

    public List<AcuerdoConfidencial> listAcuerdosConf() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct a from AcuerdoConfidencial a");
            List<AcuerdoConfidencial> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void create(AcuerdoConfidencial obj) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(obj);
            if (obj.getArchivoAcuerdoConf() != null && !obj.getArchivoAcuerdoConf().isEmpty()) {
                //si se subió el archivo, mover del directorio de temporales al original de artículos
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoAcuerdoConf());
                //String nuevoNombre = "id" + obj.getId() + "_" + obj.getArchivoAcuerdoConf();
                Path destino = ServerUtils.getPathAcuerdosConfidenciales().resolve(obj.getArchivoAcuerdoConf());
                Files.move(origen, destino, REPLACE_EXISTING);
                //FileUtils.moveFile(origen, destino);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
  
    
    public AcuerdoConfidencial findAcuerdoConfidencial(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select a from AcuerdoConfidencial a  where a.id = :id", AcuerdoConfidencial.class);
            q.setParameter("id", id);
            AcuerdoConfidencial acuerdoConfidencial = (AcuerdoConfidencial) q.getSingleResult();
            return acuerdoConfidencial;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void edit(AcuerdoConfidencial obj) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AcuerdoConfidencial acuerdoConfidencialAntiguo = em.find(AcuerdoConfidencial.class, obj.getId());
            String archivoAntiguoAcuerdoConfidencial = acuerdoConfidencialAntiguo.getArchivoAcuerdoConf();
            String archivoNuevoAcuerdoConfidencial = obj.getArchivoAcuerdoConf();
            ServerUtils.procesarAntiguoNuevoArchivo(archivoAntiguoAcuerdoConfidencial, archivoNuevoAcuerdoConfidencial, ServerUtils.getPathAcuerdosConfidenciales());
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
