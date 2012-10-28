/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.kask.projects_manager.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsers;

/**
 *
 * @author Mateusz
 */
@Stateless
public class ProjHasUsersFacade extends AbstractFacade<ProjHasUsers> {
    @PersistenceContext(unitName = "InzynierkaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProjHasUsersFacade() {
        super(ProjHasUsers.class);
    }
    
}
