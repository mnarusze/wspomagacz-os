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
import pl.gda.pg.eti.kask.projects_manager.entity.PublicationTypes;
import pl.gda.pg.eti.kask.projects_manager.facade.PublicationTypesFacade;

/**
 *
 * @author Mateusz
 */
@FacesConverter(forClass = PublicationTypes.class)
public class PublicationTypesConverter implements Converter{

    PublicationTypesFacade pubFacade = lookupPubFacadeBean();
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
//        throw new UnsupportedOperationException("Not supported yet.");
        PublicationTypes p = pubFacade.find(Integer.valueOf(value));
        return p;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
//        throw new UnsupportedOperationException("Not supported yet.");
        PublicationTypes p = (PublicationTypes) value;
        return p.getId().toString();
    }
    
    private PublicationTypesFacade lookupPubFacadeBean(){
        try {
            Context c = new InitialContext();
            return (PublicationTypesFacade) c.lookup("java:global/Inzynierka/PublicationTypesFacade");
        } catch (NamingException ex) {
            Logger.getLogger(UserConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
