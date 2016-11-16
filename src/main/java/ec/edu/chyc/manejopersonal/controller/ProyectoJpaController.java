/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.exceptions.JPAControllerException;
import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Financiamiento;
import ec.edu.chyc.manejopersonal.entity.GrupoInvestigacion;
import ec.edu.chyc.manejopersonal.entity.LineaInvestigacion;
import ec.edu.chyc.manejopersonal.entity.Lugar;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import java.util.EnumSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Hibernate;

public class ProyectoJpaController extends GenericJpaController<Proyecto> implements Serializable {
//public Proyecto findProyecto(Long id, boolean cargarFinanciamientos, boolean cargarConvenios, 
    //boolean cargarArticulos, boolean cargarTesis, boolean cargarPasantias) {    
    /*public enum Incluir {
        INC_FINANCIAMIENTOS     (0b000001),
        INC_CONVENIOS           (0b000010),
        INC_ARTICULOS           (0b000100),
        INC_TESIS               (0b001000),
        INC_PASANTIAS           (0b010000),
        INC_LUGARES             (0b100000),
        INC_TODOS               (0b111111);
     
        private final int mask;
        
        private Incluir(int mask) {
            this.mask = mask;
        }

        public int value() {
            return mask;
        }        
    }*/
    public enum Flag {
        INC_FINANCIAMIENTOS, INC_CONVENIOS, INC_ARTICULOS, INC_TESIS, INC_PASANTIAS, INC_LUGARES;

        public static final EnumSet<Flag> ALL_OPTS = EnumSet.allOf(Flag.class);
    }    
    

    public ProyectoJpaController() {
        setClassRef(Proyecto.class);
    }

    public List<GrupoInvestigacion> listGruposInvestigacion() throws JPAControllerException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct gi from GrupoInvestigacion gi order by gi.nombre asc");
            List<GrupoInvestigacion> list = q.getResultList();
            return list;
        } catch (Exception ex) {          
            throw new JPAControllerException(ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }    

    public List<LineaInvestigacion> listLineasInvestigacion() throws JPAControllerException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct li from LineaInvestigacion li order by li.nombre asc");
            List<LineaInvestigacion> list = q.getResultList();
            return list;
        } catch (Exception ex) {          
            throw new JPAControllerException(ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }    

    
    public List<Lugar> listLugares() throws JPAControllerException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct l from Lugar l order by l.nombre asc");
            List<Lugar> list = q.getResultList();
            return list;
        } catch (Exception ex) {          
            throw new JPAControllerException(ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<Proyecto> listProyecto() throws JPAControllerException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct p from Proyecto p left join fetch p.financiamientosCollection order by p.fechaFin desc, p.titulo asc");
            List<Proyecto> list = q.getResultList();
            return list;
        } catch (Exception ex) {          
            throw new JPAControllerException(ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Proyecto findProyecto(Long id, EnumSet<Flag> incluir) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            Query q = em.createQuery("select p from Proyecto p left join fetch p.contratosCollection where p.id=:id");
            q.setParameter("id", id);
            Proyecto p = (Proyecto) q.getSingleResult();
            

            if (incluir.contains(Flag.INC_CONVENIOS) || incluir.contains(Flag.INC_ARTICULOS)) {
                Hibernate.initialize(p.getConveniosCollection());
                
                if (incluir.contains(Flag.INC_ARTICULOS)) {
                    Hibernate.initialize(p.getArticulosCollection());
                }
            }
            /*if (cargarConvenios || cargarArticulos) {
                Hibernate.initialize(p.getConveniosCollection());

                if (cargarArticulos) {
                    Hibernate.initialize(p.getArticulosCollection());
                }
            }*/
            if (incluir.contains(Flag.INC_FINANCIAMIENTOS)) {
                Hibernate.initialize(p.getFinanciamientosCollection());
            }
            if (incluir.contains(Flag.INC_TESIS)){
                Hibernate.initialize(p.getTesisCollection());
                for (Tesis tesis : p.getTesisCollection()) {
                    Hibernate.initialize(tesis.getAutoresCollection());
                }
            }
            if (incluir.contains(Flag.INC_PASANTIAS)) {
                Hibernate.initialize(p.getPasantiasCollection());
            }
            if (incluir.contains(Flag.INC_LUGARES)) {
                Hibernate.initialize(p.getLugaresCollection());
            }
            return p;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void create(Proyecto proyecto) throws JPAControllerException {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            if (proyecto.getLineaInvestigacion() != null) {
                if (proyecto.getLineaInvestigacion().getId() == null || proyecto.getLineaInvestigacion().getId() < 0) {
                    proyecto.getLineaInvestigacion().setId(null);
                    em.persist(proyecto.getLineaInvestigacion());
                }
            }
            if (proyecto.getGrupoInvestigacion() != null) {
                if (proyecto.getGrupoInvestigacion().getId() == null || proyecto.getGrupoInvestigacion().getId() < 0) {
                    proyecto.getGrupoInvestigacion().setId(null);
                    em.persist(proyecto.getGrupoInvestigacion());
                }
            }
/*            
            if (proyecto.getLugar() != null) {
                if (proyecto.getLugar().getId() == null || proyecto.getLugar().getId() < 0) {
                    proyecto.getLugar().setId(null);
                    em.persist(proyecto.getLugar());
                }
            }*/
            
            em.persist(proyecto);

            for (Lugar lugar : proyecto.getLugaresCollection()) {
                if (lugar.getId() == null || lugar.getId() < 0) {
                    lugar.setId(null);
                    lugar.getProyectosCollection().add(proyecto);
                    
                    em.persist(lugar);                    
                } 
                else {
                    Lugar lugarAttached = em.find(Lugar.class, lugar.getId());
                    lugarAttached.getProyectosCollection().add(proyecto);
                }
            }            
            
            for (Financiamiento finan : proyecto.getFinanciamientosCollection()) {                
                finan.setProyecto(proyecto);

                if (finan.getInstitucion().getId() == null || finan.getInstitucion().getId() < 0) {
                    //si el codigo de la institucion es negativa o nula, es una institucion nueva
                    finan.getInstitucion().setId(null);
                    em.persist(finan.getInstitucion());
                }
                
                if (finan.getId() == null || finan.getId() < 0) {
                    finan.setId(null);
                    em.persist(finan);
                }
            }

            em.getTransaction().commit();
        } catch (Exception ex) {          
            throw new JPAControllerException(ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proyecto proyecto) throws JPAControllerException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            //quitar los elementos que ya no existen
            Proyecto proyectoAntiguo = em.find(Proyecto.class, proyecto.getId());
            for (Financiamiento finan : proyectoAntiguo.getFinanciamientosCollection()) {
                if (!proyecto.getFinanciamientosCollection().contains(finan)) {
                    em.remove(finan);
                }
            }

            //agregar los nuevos elementos
            for (Financiamiento finan : proyecto.getFinanciamientosCollection()) {
                finan.setProyecto(proyecto);
                if (finan.getId() == null || finan.getId() < 0) {
                    finan.setId(null);                    
                }
                if (finan.getInstitucion().getId() == null || finan.getInstitucion().getId() < 0) {
                    //si el codigo de la institucion es negativa o nula, es una institucion nueva
                    finan.getInstitucion().setId(null);
                    em.persist(finan.getInstitucion());
                }
                
                em.merge(finan);
            }

            //quitar elementos eliminados
            for (Lugar lugarAntiguo : proyectoAntiguo.getLugaresCollection()) {
                if (!proyecto.getLugaresCollection().contains(lugarAntiguo)) {
                    lugarAntiguo.getProyectosCollection().remove(proyecto);
                }
            }
            
            for (Lugar lugar : proyecto.getLugaresCollection()) {
                if (lugar.getId() == null || lugar.getId() < 0) {
                    lugar.setId(null);
                    lugar.getProyectosCollection().add(proyecto);
                    em.persist(lugar);
                }
            }
            
            if (proyecto.getGrupoInvestigacion() != null) {
                if (proyecto.getGrupoInvestigacion().getId() == null || proyecto.getGrupoInvestigacion().getId() < 0) {
                    proyecto.getGrupoInvestigacion().setId(null);
                    em.persist(proyecto.getGrupoInvestigacion());
                }
            }
            if (proyecto.getLineaInvestigacion() != null) {
                if (proyecto.getLineaInvestigacion().getId() == null || proyecto.getLineaInvestigacion().getId() < 0) {
                    proyecto.getLineaInvestigacion().setId(null);
                    em.persist(proyecto.getLineaInvestigacion());
                }
            }
            
            em.merge(proyecto);
            em.getTransaction().commit();
        } catch (Exception ex) {          
            throw new JPAControllerException(ex);
        } finally {
            if (em != null && em.isOpen()) {
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
