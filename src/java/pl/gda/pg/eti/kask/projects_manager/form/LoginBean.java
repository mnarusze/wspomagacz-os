/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
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
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    @Resource(name = "ldap/myLdap")
    private LdapContext ldapContext;
    
    @EJB
    private UsersFacade usersFacade;
    private Users loggedUser;
    private String username;
    private String password;
    
    public Users getLoggedUser() {
        return loggedUser;
    }
    
    public Collection<Projects> getOwnedProjects() {
        return loggedUser.getProjectsCollection1();
    }
    
    public Collection<Projects> getActiveProjects() {
        return loggedUser.getProjectsCollection();
    }

    public void setLogedUser(Users logedUser) {
        this.loggedUser = logedUser;
    }

    public String saveUser() {
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

    public String login() {
        if (loggedUser != null) {
            logout();
        }
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        try {
            
            if (username == null || username.length() == 0)
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Proszę uzupełnić login", null));
                return "failure";
            }
            if (password == null || password.length() == 0)
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Proszę uzupełnić hasło", null));
                return "failure";
            }
            
            request.login(username, password);
            
            if (!userExistsInDatabase(username)) {
                if (!addUserToDatabase(username)) {
                    return "failure";
                }
            }
            
            return "success";
            
        } catch (ServletException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Nie udało się zalogować", null));
            ex.printStackTrace();
            return "failure";
        }
    }

    private boolean userExistsInDatabase(String username) {
        List<Users> users = usersFacade.findAll();
        Iterator userIter = users.iterator();
        while (userIter.hasNext()) {
            Users user = (Users)userIter.next();
            if (user.getNickname().equals(username)) {
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
        Hashtable env = null, env2 = new Hashtable();
        try {
            env = ldapContext.getEnvironment();
            
            Iterator keysIter = env.keySet().iterator();
            Iterator valuesIter = env.values().iterator();
            while (keysIter.hasNext()) {
                String key = (String)keysIter.next();
                env2.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                if (key.equals("java.naming.security.authentication")) {
                    env2.put(Context.SECURITY_AUTHENTICATION,valuesIter.next());
                } else if (key.equals("java.naming.security.principal")) {
                    env2.put(Context.SECURITY_PRINCIPAL,valuesIter.next());
                } else if (key.equals("java.naming.provider.url")) {
                    env2.put(Context.PROVIDER_URL,valuesIter.next());
                } else if (key.equals("java.naming.security.credentials")) {
                    env2.put(Context.SECURITY_CREDENTIALS,valuesIter.next());
                }
            }
            ctx = new InitialLdapContext(env2,null);
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
                newUser.setFirstname(attrs.get("givenName").get().toString());
                newUser.setLastname(attrs.get("sn").get().toString());
                newUser.setEmail(attrs.get("mail").get().toString());
            }
            newUser.setIndeks("123456");
            newUser.setNickname(username);
            newUser.setSshkey("");
            usersFacade.create(newUser);
            List<Users> users = usersFacade.findAll();
            Iterator userIter = users.iterator();
            while (userIter.hasNext()) {
                Users user = (Users)userIter.next();
                if (user.getNickname().equals(username)) {
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
        return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null && getLoggedUser() != null;
    }

    public LoginBean() {
        logout();
    }
}
