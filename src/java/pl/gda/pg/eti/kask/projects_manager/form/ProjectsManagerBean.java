/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.ProjectsFacade;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;

/**
 *
 * @author Mateusz
 */
@ManagedBean(name = "ProjectsManagerBean")
@ViewScoped
public class ProjectsManagerBean implements Serializable {

    @EJB
    private ProjectsFacade projectsFacade;
    @EJB
    private UsersFacade usersFacade;

    public ProjectsManagerBean() {
    }

    public List<Projects> getProjects() {
        return projectsFacade.findAll();
    }

    public List<Projects> getPublicProjects() {
        return projectsFacade.findAll();
    }

    public List<Projects> projectsForOwner(Users owner) {
        List<Projects> temp = getProjects();
        List<Projects> value = new ArrayList();
        for (Projects p : temp) {
            if (p.getOwner().getNickname().equals(owner.getNickname())) {
                value.add(p);
            }
        }
        return value;
    }

    public List<Projects> projectsForActive(Users active) {
        List<Projects> temp = getProjects();
        List<Projects> value = new ArrayList();
        for (Projects p : temp) {
            for (Users u : p.getUsersCollection()) {
                if(u.getNickname().equals(active.getNickname())){
                    value.add(p);
                }
            }
        }
        return value;
    }

    public List<Users> getUsers() {
        return usersFacade.findAll();
    }

    public List<SelectItem> getUsersAsSelectItems() {
        ArrayList<SelectItem> list = new ArrayList<SelectItem>();
        for (Object o : getUsers()) {
            Users u = (Users) o;
            list.add(new SelectItem(u, u.getFirstname() + " " + u.getLastname()));
        }
        return list;
    }
}
