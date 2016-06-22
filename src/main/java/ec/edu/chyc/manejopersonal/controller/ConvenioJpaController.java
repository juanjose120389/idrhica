/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Convenio;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class ConvenioJpaController extends GenericJpaController<Convenio> implements Serializable {
    public ConvenioJpaController() {
        setClassRef(Convenio.class);
    }

    public List<Convenio> listConvenio() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select c from Convenio c");
            List<Convenio> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    
    public void create(Convenio convenio) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            if (convenio.getInstitucion() != null) {
                if (convenio.getInstitucion().getId() == null || convenio.getInstitucion().getId() < 0) {
                    convenio.getInstitucion().setId(null);
                    em.persist(convenio.getInstitucion());
                }
            }
            
            if (!convenio.getArchivoConvenio().isEmpty()) {
                //si se subió el archivo, copiar del directorio de temporales al original destino, después eliminar el archivo temporal
                Path origen = ServerUtils.getPathTemp().resolve(convenio.getArchivoConvenio());
                Path destino = ServerUtils.getPathConvenios().resolve(convenio.getArchivoConvenio());

                Files.move(origen, destino, REPLACE_EXISTING);
            }            
            
            em.persist(convenio);
            
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Convenio convenio) throws Exception {
        EntityManager em = null;
        try {            
            
            em = getEntityManager();
            em.getTransaction().begin();
            
            Convenio convenioAntiguo = em.find(Convenio.class, convenio.getId());
            
            if (convenio.getInstitucion() != null) {
                if (convenio.getInstitucion().getId() == null || convenio.getInstitucion().getId() < 0) {
                    convenio.getInstitucion().setId(null);
                    em.persist(convenio.getInstitucion());
                }
            }
            
            String archivoAntiguo = convenioAntiguo.getArchivoConvenio();
            String archivoNuevo = convenio.getArchivoConvenio();
            
            ServerUtils.procesarAntiguoNuevoArchivo(archivoAntiguo, archivoNuevo, ServerUtils.getPathConvenios());
            
            em.merge(convenio);
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
