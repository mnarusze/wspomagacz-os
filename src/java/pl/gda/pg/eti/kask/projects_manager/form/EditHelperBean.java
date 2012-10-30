/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;

/**
 *
 * @author mateusz
 */
@ManagedBean(name = "EditHeperBean")
@SessionScoped
public class EditHelperBean {
    
    
    @EJB
    private ProjectsFacade projectFacadeLocal;
    private Projects localProjects;
    private boolean editingProjects;
    
    public Projects getProjects() {
        return localProjects;
    }
    
    public String newProject() {
        localProjects = new Projects();
        editingProjects = false;
        
        return "editProject";
    }
    
}
