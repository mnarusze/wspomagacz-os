/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.kask.projects_manager.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;

/**
 *
 * @author Mateusz
 */
@Stateless
public class ProjectsFacade extends AbstractFacade<Projects> {
    @PersistenceContext(unitName = "InzynierkaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProjectsFacade() {
        super(Projects.class);
    }
    
}
