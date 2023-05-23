package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import race.system.Track;

public class TrackDao {

    private EntityManager em;

    private boolean[] freeIDs;

    public TrackDao(EntityManager em) {
        this.em = em;
        em.setFlushMode(FlushModeType.COMMIT);
        freeIDs = new boolean[2000];
        for (int i = 0; i < 2000; i++)
            freeIDs[i] = true;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Track> getAllTracks() throws HibernateException {
        try {
            em.getTransaction().begin();
            List<Track> tracks = em.createQuery("FROM Track", Track.class).getResultList();
            em.getTransaction().commit();
            return tracks;

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public Track findTrack(int id) {
        em.getTransaction().begin();
        Track track = em.find(Track.class, id);
        em.getTransaction().commit();
        return track;
    }

    public void saveTrack(Track track) {
        try {
            em.getTransaction().begin();
            em.persist(track);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }

    }

    public void updateTrackID(Track track, Integer value) {
        em.getTransaction().begin();
        track.setTrackID(value);
        em.getTransaction().commit();
    }

    public void updateTrack(Track track) {
        try {
            em.getTransaction().begin();
            em.merge(track);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void deleteTrack(Track track) {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Track t WHERE trackID = :id", null).setParameter("id", track.getTrackID())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearTrack() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Track").executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public int getFreeID() {
        int id = 0;
        em.getTransaction().begin();
        for (int i = 0; i < freeIDs.length; i++) {
            if (freeIDs[i]) {
                id = i;
                break;
            }
        }
        em.getTransaction().commit();
        return id;
    }

    public void addFreeID(int id) {
        freeIDs[id] = true;
    }

    public void updateFreeID(List<Track> tracks) {
        for (Track track : tracks) {
            freeIDs[track.getTrackID()] = false;
        }
    }
}
