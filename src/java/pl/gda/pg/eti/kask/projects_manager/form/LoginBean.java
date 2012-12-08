/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsers;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;
import pl.gda.pg.eti.kask.projects_manager.managers.ProjectsManager;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    @EJB
    private UsersFacade usersFacade;
    private Users loggedUser;
    private String username;
    private String password;

    public Users getLoggedUser() {
        return loggedUser;
    }

    public Collection<Projects> getOwnedProjects() {
        Collection<Projects> result = null;
        for (ProjHasUsers projects : loggedUser.getProjHasUsersCollection()) {
            if (projects.getRola().getId().equals(1)) {
                result.add(projects.getProjects());
            }
        }
        return result;
    }

    public Collection<Projects> getActiveProjects() {
        Collection<Projects> result = null;
        for (ProjHasUsers projects : loggedUser.getProjHasUsersCollection()) {
            if (projects.getRola().getId().equals(2)) {
                result.add(projects.getProjects());
            }
        }
        return result;
    }

    public void setLoggedUser(Users loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String saveUser(String action) {
        if (action.equals("ssh")) {
            if (ProjectsManager.changeSSHKey(loggedUser, loggedUser.getSshkey()) != true) {
                FacesContext.getCurrentInstance().addMessage("errorMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operacja zmiany klucza SSH nie powiodła się; prosimy o kontakt z aministratorem", null));
                return "my_account";
            }
        }
        FacesContext.getCurrentInstance().addMessage("successMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Operacja zmiany klucza SSH powiodła się!", null));
        usersFacade.edit(loggedUser);
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
        if (loggedUser.getSshkey() != null) {
            if (loggedUser.getSshkey().isEmpty()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public String deleteSshKey() {
        loggedUser.setSshkey("");
        usersFacade.edit(loggedUser);
        return "my_account";
    }

    public String loginBackdoor() {
        List<Users> users = usersFacade.findAll();
        setLoggedUser(users.get(0));
        return "success";
    }

    public String login() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        try {
            if (request.getUserPrincipal() != null) {
                request.logout();
            }

            if (username == null || username.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Proszę uzupełnić login", null));
                return "failure";
            }
            if (password == null || password.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Proszę uzupełnić hasło", null));
                return "failure";
            }

            request.login(username, password);

            if (!userExistsInDatabase(username)) {
                if (!addUserToDatabase(username)) {
                    request.logout();
                    return "failure";
                }
            }
            
            return "success";

        } catch (ServletException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nie udało się zalogować", null));
            ex.printStackTrace();
            return "failure";
        }
    }

    private boolean userExistsInDatabase(String username) {
        List<Users> users = usersFacade.findAll();
        Iterator userIter = users.iterator();
        while (userIter.hasNext()) {
            Users user = (Users) userIter.next();
            if (user.getLogin().equals(username)) {
                loggedUser = user;
                return true;
            }
        }
        return false;
    }

    private SearchControls getSimpleSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(30000);
        return searchControls;
    }

    private boolean addUserToDatabase(String username) {
        LdapContext ctx = null;
        Users newUser = new Users();
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://192.168.56.2:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "CN=linux-user-1,CN=Users,DC=ad,DC=inzynierka,DC=com");
        env.put(Context.SECURITY_CREDENTIALS, "haslo-123");

        try {
            ctx = new InitialLdapContext(env, null);
            NamingEnumeration<?> userInfo = ctx.search("CN=Users,DC=ad,DC=inzynierka,DC=com", "(&(objectclass=user)(sAMAccountName=" + username + "))", getSimpleSearchControls());
            while (userInfo.hasMore()) {
                SearchResult result = (SearchResult) userInfo.next();
                Attributes attrs = result.getAttributes();
                if (attrs.get("givenName") == null || attrs.get("givenName").get() == null || attrs.get("givenName").get().toString().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Brakuje informacji w bazie AD: GivenName", null));
                    return false;
                }
                if (attrs.get("sn") == null || attrs.get("sn").get() == null || attrs.get("sn").get().toString().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Brakuje informacji w bazie AD: SN", null));
                    return false;
                }
                if (attrs.get("mail") == null || attrs.get("mail").get() == null || attrs.get("mail").get().toString().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Brakuje informacji w bazie AD: Mail", null));
                    return false;
                }
                newUser.setFirstName(attrs.get("givenName").get().toString());
                newUser.setLastName(attrs.get("sn").get().toString());
                newUser.setEmail(attrs.get("mail").get().toString());
            }
            newUser.setIndeks("123456");
            newUser.setLogin(username);
            newUser.setSshkey("");
            usersFacade.create(newUser);
            List<Users> users = usersFacade.findAll();
            Iterator userIter = users.iterator();
            while (userIter.hasNext()) {
                Users user = (Users) userIter.next();
                if (user.getLogin().equals(username)) {
                    loggedUser = user;
                    break;
                }
            }
        } catch (NamingException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public void userLogout() {
        loggedUser.setLastLogin(new Date());
        usersFacade.edit(loggedUser);
        logout();
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
        loggedUser = null;
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().
                handleNavigation(FacesContext.getCurrentInstance(), null, "/index.xhtml");
    }

    public boolean isLogged() {
        //return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null && getLoggedUser() != null;
        return getLoggedUser() != null;
    }

    public LoginBean() {
        logout();
    }
}
