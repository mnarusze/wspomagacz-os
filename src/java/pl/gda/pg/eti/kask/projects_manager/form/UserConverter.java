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
import pl.gda.pg.eti.kask.projects_manager.entity.Users;
import pl.gda.pg.eti.kask.projects_manager.facade.UsersFacade;

/**
 *
 * @author Mateusz
 */
@FacesConverter(forClass = Users.class)
public class UserConverter implements Converter {
    
    UsersFacade usersFacade = lookupUsersFacadeBean();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        //throw new UnsupportedOperationException("Not supported yet.");
        Users u = usersFacade.find(Short.valueOf(value));
        return u;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        //throw new UnsupportedOperationException("Not supported yet.");
        Users u = (Users) value;
        
        return u.getId().toString();
    }
    
    private UsersFacade lookupUsersFacadeBean() {
        try {
            Context c = new InitialContext();
            return (UsersFacade) c.lookup("java:global/Inzynierka/UsersFacade");
        } catch (NamingException ex) {
            Logger.getLogger(UserConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
