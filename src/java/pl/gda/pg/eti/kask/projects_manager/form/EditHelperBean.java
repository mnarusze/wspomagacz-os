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
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;
import pl.gda.pg.eti.kask.projects_manager.managers.RepositoriesManager;

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
    @EJB
    private UsersFacade usersFacadeLocal;
    private Users localUsers;
    private boolean editingUsers;

    public EditHelperBean() {
    }

    public Projects getProjects() {
        return localProjects;
    }

    public Users getUser() {
        return localUsers;
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

    public String addUsersToProject() {
        localProjects = (Projects) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Projects");


        return "users_list";

    }

    public String saveUsersToProject() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Users");

        localProjects.getUsersCollection().add(localUsers);

        projectFacadeLocal.edit(localProjects);
        return "projects_list";
    }

    public String removeUsersFromProject() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Users");

        localProjects.getUsersCollection().remove(localUsers);

        projectFacadeLocal.edit(localProjects);
        return "projects_list";
    }

    public String deleteUsers() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("User");

        usersFacadeLocal.remove(localUsers);
        projectFacadeLocal.edit(localProjects);
        return "projects_list";
    }

    public String editUsers() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("User");
        editingUsers = true;

        return "edit_user";
    }

    public String newUser() {
        localUsers = new Users();
        editingUsers = false;

        return "edit_user";
    }
    
    public String saveUser() {
        if (editingUsers) {
            usersFacadeLocal.edit(localUsers);
        } else {
            usersFacadeLocal.create(localUsers);
        }
        return "projects_list";
    }

    public String saveProject() {
        if (editingProjects) {
            projectFacadeLocal.edit(localProjects);
        } else {
            if (localProjects.getGitEnabled()) {
                RepositoriesManager.createRepository(localProjects.getProjName(), "git");
            }
            if (localProjects.getSvnEnabled()) {
                RepositoriesManager.createRepository(localProjects.getProjName(), "svn");
            }
            projectFacadeLocal.create(localProjects);

        }

        return "projects_list";
    }

    public String usunProject() {
        localProjects = (Projects) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Projects");
        projectFacadeLocal.remove(localProjects);
        if (localProjects.getGitEnabled()) {
            RepositoriesManager.deleteRepository(localProjects.getProjName(), "git");
        }
        if (localProjects.getSvnEnabled()) {
            RepositoriesManager.deleteRepository(localProjects.getProjName(), "svn");
        }       
        return "projects_list";
    }
}