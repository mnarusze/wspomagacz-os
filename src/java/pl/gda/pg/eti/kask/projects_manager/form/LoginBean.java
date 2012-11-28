/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import com.sun.jndi.url.ldap.ldapURLContext;
import java.io.Serializable;
import java.util.Collection;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    @EJB
    private UsersFacade usersFacade;
    private Users logedUser;
    boolean zalogowany = false;
    private String username;
    private String password;

    public void reloadLogedUser() {
        logedUser = (Users) usersFacade.findAll().get(0);
    }
    
    public Users getLogedUser() {
        return logedUser;
    }
    
    public Collection<Projects> getOwnedProjects() {
        reloadLogedUser();
        return logedUser.getProjectsCollection1();
    }
    
    public Collection<Projects> getActiveProjects() {
        return logedUser.getProjectsCollection();
    }

    public void setLogedUser(Users logedUser) {
        this.logedUser = logedUser;
    }

    public String saveUser() {
        usersFacade.edit(logedUser);
        return "my_account";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasPublicKey() {
        if (logedUser.getSshkey() != null) {
            if (logedUser.getSshkey().isEmpty()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public String deleteSshKey() {
        logedUser.setSshkey("");
        usersFacade.edit(logedUser);
        return "my_account";
    }

    public String login() {
        reloadLogedUser();
        zalogowany = true;
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        try {
            
            request.login(username, password);
            
            setUsername(username);

            return "success";

        } catch (ServletException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login failed", null));
            ex.printStackTrace();
        }
        return "failure";
    }

    public void logout() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            try {
                request.logout();
                session.invalidate();
            } catch (ServletException ex) {
                ex.printStackTrace();
            }
        }
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().
                handleNavigation(FacesContext.getCurrentInstance(), null, "/index.xhtml");
    }

    public boolean isLogged() {
        //return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null;
        return zalogowany;
    }

    public LoginBean() {
        logout();
    }
}
