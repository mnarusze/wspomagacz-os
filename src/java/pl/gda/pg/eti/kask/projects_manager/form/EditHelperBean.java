/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;

/**
 *
 * @author mateusz
 */
@ManagedBean(name = "EditHelperBean")
@SessionScoped
public class EditHelperBean {

    @EJB
    private ProjectsFacade projectFacadeLocal;
    private Projects localProjects;
    private boolean editingProjects;

    public EditHelperBean() {
    }

    public Projects getProjects() {
        return localProjects;
    }

    public String newProject() {
        localProjects = new Projects();
        editingProjects = false;

        return "edit_projects";
    }

    public String editProject() {
        localProjects = (Projects) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Projects");
        editingProjects = true;

        return "edit_projects";

    }

    public String saveProject() {
        if (editingProjects) {
            projectFacadeLocal.edit(localProjects);
        } else {
            projectFacadeLocal.create(localProjects);
        }

        return "projects_list";
    }

    public String usunProject() {
        localProjects = (Projects) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Projects");
        projectFacadeLocal.remove(localProjects);
        return "projects_list";
    }
}