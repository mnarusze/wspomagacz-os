package pl.gda.pg.eti.kask.projects_manager.authentication;

import org.apache.shiro.realm.ldap.JndiLdapRealm;

public class AuthenticationManager {
    
    private JndiLdapRealm ldapRealm;
    
    public AuthenticationManager() {
       // ldapRealm = new JndiLdapRealm();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                b
    }
    
    public boolean userExists(String ident, String password) {
        return true;
    }
}
