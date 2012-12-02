/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsers;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.PublicationTypes;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.PublicationTypesFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;

/**
 *
 * @author Mateusz
 */
@Named
@ViewScoped
public class ProjectsManagerBean implements Serializable {

    @EJB
    private ProjectsFacade projectsFacade;
    @EJB
    private UsersFacade usersFacade;
    @EJB
    private PublicationTypesFacade publicationFacade;

    public ProjectsManagerBean() {
    }

    public List<Projects> getProjects() {
        return projectsFacade.findAll();
    }

    public List<Projects> publicProjects(Users user) {
        List<Projects> temp = getProjects();
        List<Projects> value = new ArrayList<Projects>();
        for (Projects projects : temp) {
            if (!projects.getPubType().isHidden()) {
                value.add(projects);
//            } else if (user != null) {
//                if (projects.getOwner().getNickname().equals(user.getNickname())) {
//                    value.add(projects);
//                } else {
//                    for (Users u : projects.getUsersCollection()) {
//                        if(u.getNickname().equals(user.getNickname())){
//                            value.add(projects);
//                        }
//                    }
//                }
            }
        }
        return value;
    }

    public List<Projects> projectsForOwner(Users owner) {
//        List<Projects> temp = getProjects();
        List<Projects> value = new ArrayList();
//        for (Projects p : temp) {
//            if (p.getOwner().getNickname().equals(owner.getNickname())) {
//                value.add(p);
//            }
//        }
        for (ProjHasUsers projects : owner.getProjHasUsersCollection()) {
            if (projects.getRola().getId().equals(1)) {
                value.add(projects.getProjects());
            }
        }
        return value;
    }

    public List<Projects> projectsForActive(Users active) {
//        List<Projects> temp = getProjects();
        List<Projects> value = new ArrayList();
//        for (Projects p : temp) {
//            for (Users u : p.getUsersCollection()) {
//                if (u.getNickname().equals(active.getNickname())) {
//                    value.add(p);
//                }
//            }
//        }
        for (ProjHasUsers projects : active.getProjHasUsersCollection()) {
            if (projects.getRola().getId().equals(2)) {
                value.add(projects.getProjects());
            }
        }
        return value;
    }

    public List<Users> getUsers() {
        return usersFacade.findAll();
    }

    public Collection<ProjHasUsers> usersActiveInProject(Projects p) {
//        List<Users> result = new ArrayList<Users>();
//        for (ProjHasUsers object : p.getProjHasUsersCollection()) {
//            if(object.getRola().getId().equals(2))
//            {
//                result.add(object.getUsers());
//            }
//        }
//        return result;
        return p.getProjHasUsersCollection();
    }

    public List<Users> usersReadOnlyInProject(Projects p) {
//        List<Users> result = new ArrayList<Users>();
//        for (ProjHasUsers object : p.getProjHasUsersCollection()) {
//            if (object.getRola().getId().equals(3)) {
//                result.add(object.getUsers());
//            }
//        }
//        return result;
        return p.getGuests();
    }

    public List<SelectItem> getUsersAsSelectItems() {
        ArrayList<SelectItem> list = new ArrayList<SelectItem>();
        for (Object o : getUsers()) {
            Users u = (Users) o;
            list.add(new SelectItem(u, u.getFirstName() + " " + u.getLastName()));
        }
        return list;
    }
    
    public List<PublicationTypes> getPublicationTypes(){
        return publicationFacade.findAll();
    }

    public List<SelectItem> getPublicationTypesAsSelectItems() {
        ArrayList<SelectItem> list = new ArrayList<SelectItem>();
        for (Object o : getPublicationTypes()) {
            PublicationTypes u = (PublicationTypes) o;
            list.add(new SelectItem(u, u.getPublicationDescription()));
        }
        return list;
    }
}
