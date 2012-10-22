package pl.gda.pg.eti.kask.projects_manager.authentication;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public class AuthenticationManager {
    
    private Factory<SecurityManager> ldapFactory;
    private SecurityManager sManager;
    
    
    public AuthenticationManager() {
        ldapFactory = new IniSecurityManagerFactory("classpath:active_directory_conf.ini");
        sManager = ldapFactory.getInstance();
        SecurityUtils.setSecurityManager(sManager);
    }
    
    public Subject loginUser(String username, String password) {
        Subject user = SecurityUtils.getSubject();
        AuthenticationToken token =
            new UsernamePasswordToken(username, password);
        try {
            user.login(token);
        } catch (IncorrectCredentialsException ice) {
            return null;
        } catch (LockedAccountException lae) {
            return null;
        } catch (AuthenticationException ae) {
            return null;
        }
        return user;
    }    
}
