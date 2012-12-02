/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.form;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import pl.gda.pg.eti.kask.projects_manager.entity.UserRoles;
import pl.gda.pg.eti.kask.projects_manager.facade.UserRolesFacade;

/**
 *
 * @author Mateusz
 */
@FacesConverter(forClass = UserRoles.class)
public class UserRoleConverter implements Converter {

    UserRolesFacade ur = lookupUserRolesFacadeBean();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        UserRoles u = ur.find(Integer.valueOf(value));
        return u;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
//        throw new UnsupportedOperationException("Not supported yet.");
        UserRoles u = (UserRoles) value;

        return u.getId().toString();
    }

    private UserRolesFacade lookupUserRolesFacadeBean() {
        try {
            Context c = new InitialContext();
            return (UserRolesFacade) c.lookup("java:global/Inzynierka/UserRolesFacade");
        } catch (NamingException ex) {
            Logger.getLogger(UserConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
