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
import pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsers;
import pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsersPK;
import pl.gda.pg.eti.kask.projects_manager.entity.ProjectDescription;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.UserRoles;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjHasUsersFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectDescriptionFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;
import pl.gda.pg.eti.kask.projects_manager.managers.ProjectsManager;

/**
 *
 * @author mateusz
 */
@Named
@ConversationScoped
public class EditHelperBean implements Serializable {

    @EJB
    private ProjectsFacade projectFacadeLocal;
    private Projects localProjects;
    private UserRoles localRoles;
    private ProjectDescription editingProjDescription;
    private boolean editingProjects;
    @EJB
    private ProjHasUsersFacade projectUsersFacadeLocal;
    @EJB
    private ProjectDescriptionFacade projectDescriptionFacadeLocal;
    @EJB
    private UsersFacade usersFacadeLocal;
    private Users localUsers;
    private boolean editingUsers;
    @Inject
    Conversation conversation;

    public Projects getProjects() {
        return localProjects;
    }

    public Users getLocalUsers() {
        return localUsers;
    }

    public void setLocalUsers(Users localUsers) {
        this.localUsers = localUsers;
    }

    private void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void endConversation() {
        if (!conversation.isTransient()) {
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

    public ProjectDescription getEditingProjDescription() {
        return editingProjDescription;
    }

    public void setEditingProjDescription(ProjectDescription editingProjDescription) {
        this.editingProjDescription = editingProjDescription;
    }

    public String editProjDescription(ProjectDescription proj) {
        editingProjDescription = proj;

        return "edit_project_description";

    }

    public String saveProjDescription() {
        projectDescriptionFacadeLocal.edit(editingProjDescription);

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

//        if (ProjectsManager.addUser(localProjects, localUsers)) {
//            localProjects.getReadOnlyUsersCollection().add(localUsers);
//        }


        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String removeReadOnlyUsers(Users u) {
        localUsers = u;

//        if (ProjectsManager.removeUser(localProjects, localUsers)) {
//            localProjects.getReadOnlyUsersCollection().remove(localUsers);
//        }
        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String saveUsersToProject() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Users");

//        if (ProjectsManager.addUser(localProjects, localUsers)) {
//            localProjects.getUsersCollection().add(localUsers);
//        }


        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public boolean isOwnerInProject(Projects p, Users u) {
        for (ProjHasUsers pu : p.getProjHasUsersCollection()) {
            if (pu.getRola().getId().equals(1)) {
                if (pu.getUsers().getLogin().equals(u.getLogin())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String display(Projects p, Users u) {
        if (u != null) {
            if (isOwnerInProject(p, u)) {
                return viewProject(p);
            } else if (p.getIsPariatlyPublic() || p.getIsPublic()) {
                return viewProject(p);
            } else {
                return "accessdenied";
            }
        } else if (p.getIsPublic()) {

            return viewProject(p);
        } else {
            return "accessdenied";
        }
    }

    public String saveUsersToProject(Users u, UserRoles roles) {
        localUsers = u;
        localProjects.addUserPerRole(u, roles.getId());
//        ProjHasUsers phu;
//        phu = new ProjHasUsers();
//        phu.setUsers(u);
//        phu.setProjects(localProjects);
//        UserRoles ur = new UserRoles();
//        ur.setId(2);
//        phu.setRola(roles);
//        phu.setProjHasUsersPK(new ProjHasUsersPK(localProjects.getId(), u.getId()));
//        localProjects.getProjHasUsersCollection().add(phu);
//        if (ProjectsManager.addUser(localProjects, localUsers)) {
//            localProjects.getUsersCollection().add(localUsers);
//        }


        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String removeUsersFromProject() {
        localUsers = (Users) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap()
                .get("Users");

//        if (ProjectsManager.removeUser(localProjects, localUsers)) {
//            localProjects.getUsersCollection().remove(localUsers);
//        }
        projectFacadeLocal.edit(localProjects);
        return "edit_projects";
    }

    public String removeUsersFromProject(Users u) {
        localUsers = u;

//        if (ProjectsManager.removeUser(localProjects, localUsers)) {
//            localProjects.getUsersCollection().remove(localUsers);
//        }

//        try {
//            for (ProjHasUsers pk : localProjects.getProjHasUsersCollection()) {
//                if (pk.getUsers().getLogin().equals(u.getLogin())) {
//                    projectUsersFacadeLocal.remove(pk);
//                }
//            }
//        } catch (Exception e) {
//        }
        localProjects.removeUserFromProject(u);
        
        projectFacadeLocal.edit(localProjects);

        //pk.setProjHasUsersPK(new ProjHasUsersPK(localProjects.getId(), u.getId()));


        //localProjects.getProjHasUsersCollection().remove(pk);
        //usersFacadeLocal.edit(u);
        //projectFacadeLocal.edit(localProjects);
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

    private void saveNewProject(Users owner) {
        ProjectDescription desc = new ProjectDescription();
        desc.setProjFullName(localProjects.getProjName());
//        projectDescriptionFacadeLocal.create(desc);
        localProjects.setProjDescription(desc);

//        localProjects.addUserPerRole(owner, 1);
        projectFacadeLocal.create(localProjects);
        localProjects.addUserPerRole(owner, 1);
        projectFacadeLocal.edit(localProjects);
    }

    private void saveEditingProject() {
        projectFacadeLocal.edit(localProjects);
    }

    public String saveProject(Users owner) {
        if (editingProjects) {
            saveEditingProject();
        } else {
//            if (ProjectsManager.createProject(localProjects) != true) {
//                FacesContext.getCurrentInstance().addMessage("errorMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operacja utworzenia projektu nie powiodła się; prosimy o kontakt z aministratorem", null));
//            } else {
//                localProjects.setProjDescription(new ProjectDescription(localProjects.getProjName()));
//                projectFacadeLocal.create(localProjects);
            saveNewProject(owner);
            FacesContext.getCurrentInstance().addMessage("infoMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Pomyślnie utworzono projekt " + localProjects.getProjName(), null));
//            }
        }

        endConversation();

        return "my_projects";
    }

    public String usunProject() {

//        if (ProjectsManager.deleteProject(localProjects) != true) {
//            FacesContext.getCurrentInstance().addMessage("errorMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operacja usunięcia projektu nie powiodła się; prosimy o kontakt z aministratorem", null));
//        } else {
        projectFacadeLocal.remove(localProjects);
//            FacesContext.getCurrentInstance().addMessage("infoMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Pomyślnie usunięto projekt " + localProjects.getProjName(), null));
//        }

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

    public UserRoles getLocalRoles() {
        return localRoles;
    }

    public void setLocalRoles(UserRoles localRoles) {
        this.localRoles = localRoles;
    }

    @Remove
    public void destroy() {
        endConversation();
    }
}