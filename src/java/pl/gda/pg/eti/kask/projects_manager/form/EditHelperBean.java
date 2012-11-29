    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;
import pl.gda.pg.eti.kask.projects_manager.managers.ProjectsManager;

/**
 *
 * @author mateusz
 */
@Named("EditHelperBean")
@ConversationScoped
public class EditHelperBean implements Serializable {

    @EJB
    private ProjectsFacade projectFacadeLocal;
    private Projects localProjects;
    private boolean editingProjects;
    @EJB
    private UsersFacade usersFacadeLocal;
    private Users localUsers;
    private boolean editingUsers;
    @Inject
    Conversation conversation;

    public EditHelperBean() {
    }

    public Projects getProjects() {
        return localProjects;
    }

    private void beginConversation()
    {
        if (conversation.isTransient())
        {
            conversation.begin();
        }
    }
 
    private void endConversation()
    {
        if (!conversation.isTransient())
        {
            conversation.end();
        }
    }
    
    public Users getUser() {
        return localUsers;
    }

    public String newProject() {
        beginConversation();

        localProjects = new Projects();
        editingProjects = false;


        return "edit_projects";
    }

    public String editProject(Projects p) {
        beginConversation();

        localProjects = p;
        editingProjects = true;


        return "edit_projects";

    }

    public String editProject() {
        
        beginConversation();

        localProjects = (Projects) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Projects");
        editingProjects = true;


        return "edit_projects";

    }

    public String viewProject() {
        localProjects = (Projects) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Projects");

        //localProjects = p;
        //editingProjects = true;

        return "project_view";

    }

    public String viewProject(Projects p) {
//        localProjects = (Projects) FacesContext.getCurrentInstance()
//                .getExternalContext().getRequestMap()
//                .get("Projects");

        localProjects = p;
        //editingProjects = true;

        return "project_view";
    }

    public String addUsersToProject() {

        localProjects = (Projects) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Projects");


        return "users_list";
    }

    public String addUsersToProject(Projects p) {
        localProjects = p;

        return "users_list";
    }

    public String addReadOnlyUsers(Projects p) {
        localProjects = p;

        return "read_only_users_list";
    }

    public String saveReadOnlyUsers(Users u) {
        localUsers = u;

        if (ProjectsManager.addUser(localProjects, localUsers)) {
        localProjects.getUsersReadOnlyCollection().add(localUsers);
        }


        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String removeReadOnlyUsers(Users u) {
        localUsers = u;

        if (ProjectsManager.removeUser(localProjects, localUsers)) {
        localProjects.getUsersReadOnlyCollection().remove(localUsers);
        }
        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String saveUsersToProject() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Users");

        if (ProjectsManager.addUser(localProjects, localUsers)) {
            localProjects.getUsersCollection().add(localUsers);
        }


        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String display(Projects p, Users u) {
        if (u != null) {
            if (p.getOwner().getNickname().equals(u.getNickname())) {
                return editProject(p);
            } else {
                return viewProject(p);
            }
        } else {
            return viewProject(p);
        }
    }

    public String saveUsersToProject(Users u) {
        localUsers = u;

        if (ProjectsManager.addUser(localProjects, localUsers)) {
            localProjects.getUsersCollection().add(localUsers);
        }


        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String removeUsersFromProject() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Users");

        if (ProjectsManager.removeUser(localProjects, localUsers)) {
            localProjects.getUsersCollection().remove(localUsers);
        }
        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String removeUsersFromProject(Users u) {
        localUsers = u;

        if (ProjectsManager.removeUser(localProjects, localUsers)) {
            localProjects.getUsersCollection().remove(localUsers);
        }
        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
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
            if (ProjectsManager.createProject(localProjects) != true) {
                FacesContext.getCurrentInstance().addMessage("errorMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operacja utworzenia projektu nie powiodła się; prosimy o kontakt z aministratorem", null));
            } else {
                projectFacadeLocal.create(localProjects);
                FacesContext.getCurrentInstance().addMessage("infoMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Pomyślnie utworzono projekt " + localProjects.getProjName(), null));
            }
        }

        endConversation();

        return "my_projects";
    }

    public String usunProject() {

        if (ProjectsManager.deleteRepository(localProjects)) {
            projectFacadeLocal.remove(localProjects);
        }

        endConversation();
        return "my_projects";
    }

    public String anuluj() {

        endConversation();
        return "my_projects";
    }

    public boolean isEditingProjects() {
        return editingProjects;
    }

    public void setEditingProjects(boolean editingProjects) {
        this.editingProjects = editingProjects;
    }
    
    @Remove
    public void destroy()
    {
        endConversation();
    }
}