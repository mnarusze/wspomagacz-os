/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;

/**
 *
 * @author Mateusz
 */
@ManagedBean(name="ProjectsManagerBean")
public class ProjectsManagerBean implements Serializable {
    
    @EJB
    private ProjectsFacade projectsFacade;

    public ProjectsManagerBean() {
    }
    
    public List<Projects> getProjects() {
        return projectsFacade.findAll();
    }
    
    
}
