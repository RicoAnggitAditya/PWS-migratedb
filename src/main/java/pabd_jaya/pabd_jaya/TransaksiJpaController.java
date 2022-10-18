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
import javax.persistence.Persistence;
import pabd_jaya.pabd_jaya.exceptions.IllegalOrphanException;
import pabd_jaya.pabd_jaya.exceptions.NonexistentEntityException;
import pabd_jaya.pabd_jaya.exceptions.PreexistingEntityException;

/**
 *
 * @author acer
 */
public class TransaksiJpaController implements Serializable {

    public TransaksiJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("pabd_jaya_pabd_jaya_jar_0.0.1-SNAPSHOTPU");

    public TransaksiJpaController() {
    }
    
    

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Transaksi transaksi) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Obat obatOrphanCheck = transaksi.getObat();
        if (obatOrphanCheck != null) {
            Transaksi oldTransaksiOfObat = obatOrphanCheck.getTransaksi();
            if (oldTransaksiOfObat != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Obat " + obatOrphanCheck + " already has an item of type Transaksi whose obat column cannot be null. Please make another selection for the obat field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Obat obat = transaksi.getObat();
            if (obat != null) {
                obat = em.getReference(obat.getClass(), obat.getKodeObat());
                transaksi.setObat(obat);
            }
            em.persist(transaksi);
            if (obat != null) {
                obat.setTransaksi(transaksi);
                obat = em.merge(obat);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaksi(transaksi.getKodeTransaksi()) != null) {
                throw new PreexistingEntityException("Transaksi " + transaksi + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Transaksi transaksi) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaksi persistentTransaksi = em.find(Transaksi.class, transaksi.getKodeTransaksi());
            Obat obatOld = persistentTransaksi.getObat();
            Obat obatNew = transaksi.getObat();
            List<String> illegalOrphanMessages = null;
            if (obatNew != null && !obatNew.equals(obatOld)) {
                Transaksi oldTransaksiOfObat = obatNew.getTransaksi();
                if (oldTransaksiOfObat != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Obat " + obatNew + " already has an item of type Transaksi whose obat column cannot be null. Please make another selection for the obat field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (obatNew != null) {
                obatNew = em.getReference(obatNew.getClass(), obatNew.getKodeObat());
                transaksi.setObat(obatNew);
            }
            transaksi = em.merge(transaksi);
            if (obatOld != null && !obatOld.equals(obatNew)) {
                obatOld.setTransaksi(null);
                obatOld = em.merge(obatOld);
            }
            if (obatNew != null && !obatNew.equals(obatOld)) {
                obatNew.setTransaksi(transaksi);
                obatNew = em.merge(obatNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = transaksi.getKodeTransaksi();
                if (findTransaksi(id) == null) {
                    throw new NonexistentEntityException("The transaksi with id " + id + " no longer exists.");
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
            Transaksi transaksi;
            try {
                transaksi = em.getReference(Transaksi.class, id);
                transaksi.getKodeTransaksi();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaksi with id " + id + " no longer exists.", enfe);
            }
            Obat obat = transaksi.getObat();
            if (obat != null) {
                obat.setTransaksi(null);
                obat = em.merge(obat);
            }
            em.remove(transaksi);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Transaksi> findTransaksiEntities() {
        return findTransaksiEntities(true, -1, -1);
    }

    public List<Transaksi> findTransaksiEntities(int maxResults, int firstResult) {
        return findTransaksiEntities(false, maxResults, firstResult);
    }

    private List<Transaksi> findTransaksiEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Transaksi.class));
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

    public Transaksi findTransaksi(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaksi.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransaksiCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Transaksi> rt = cq.from(Transaksi.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
