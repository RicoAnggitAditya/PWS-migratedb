/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pabd_jaya.pabd_jaya;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import pabd_jaya.pabd_jaya.exceptions.IllegalOrphanException;
import pabd_jaya.pabd_jaya.exceptions.NonexistentEntityException;
import pabd_jaya.pabd_jaya.exceptions.PreexistingEntityException;

/**
 *
 * @author acer
 */
public class SupplierJpaController implements Serializable {

    public SupplierJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Supplier supplier) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Obat obatOrphanCheck = supplier.getObat();
        if (obatOrphanCheck != null) {
            Supplier oldSupplierOfObat = obatOrphanCheck.getSupplier();
            if (oldSupplierOfObat != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Obat " + obatOrphanCheck + " already has an item of type Supplier whose obat column cannot be null. Please make another selection for the obat field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Obat obat = supplier.getObat();
            if (obat != null) {
                obat = em.getReference(obat.getClass(), obat.getKodeObat());
                supplier.setObat(obat);
            }
            em.persist(supplier);
            if (obat != null) {
                obat.setSupplier(supplier);
                obat = em.merge(obat);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSupplier(supplier.getIdPerusahaan()) != null) {
                throw new PreexistingEntityException("Supplier " + supplier + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Supplier supplier) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Supplier persistentSupplier = em.find(Supplier.class, supplier.getIdPerusahaan());
            Obat obatOld = persistentSupplier.getObat();
            Obat obatNew = supplier.getObat();
            List<String> illegalOrphanMessages = null;
            if (obatNew != null && !obatNew.equals(obatOld)) {
                Supplier oldSupplierOfObat = obatNew.getSupplier();
                if (oldSupplierOfObat != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Obat " + obatNew + " already has an item of type Supplier whose obat column cannot be null. Please make another selection for the obat field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (obatNew != null) {
                obatNew = em.getReference(obatNew.getClass(), obatNew.getKodeObat());
                supplier.setObat(obatNew);
            }
            supplier = em.merge(supplier);
            if (obatOld != null && !obatOld.equals(obatNew)) {
                obatOld.setSupplier(null);
                obatOld = em.merge(obatOld);
            }
            if (obatNew != null && !obatNew.equals(obatOld)) {
                obatNew.setSupplier(supplier);
                obatNew = em.merge(obatNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = supplier.getIdPerusahaan();
                if (findSupplier(id) == null) {
                    throw new NonexistentEntityException("The supplier with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Supplier supplier;
            try {
                supplier = em.getReference(Supplier.class, id);
                supplier.getIdPerusahaan();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The supplier with id " + id + " no longer exists.", enfe);
            }
            Obat obat = supplier.getObat();
            if (obat != null) {
                obat.setSupplier(null);
                obat = em.merge(obat);
            }
            em.remove(supplier);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Supplier> findSupplierEntities() {
        return findSupplierEntities(true, -1, -1);
    }

    public List<Supplier> findSupplierEntities(int maxResults, int firstResult) {
        return findSupplierEntities(false, maxResults, firstResult);
    }

    private List<Supplier> findSupplierEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Supplier.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Supplier findSupplier(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Supplier.class, id);
        } finally {
            em.close();
        }
    }

    public int getSupplierCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Supplier> rt = cq.from(Supplier.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
