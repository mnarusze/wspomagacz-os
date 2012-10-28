/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mateusz
 */
@Entity
@Table(name = "proj_has_users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjHasUsers.findAll", query = "SELECT p FROM ProjHasUsers p"),
    @NamedQuery(name = "ProjHasUsers.findByProjName", query = "SELECT p FROM ProjHasUsers p WHERE p.projHasUsersPK.projName = :projName"),
    @NamedQuery(name = "ProjHasUsers.findByUserName", query = "SELECT p FROM ProjHasUsers p WHERE p.projHasUsersPK.userName = :userName")})
public class ProjHasUsers implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProjHasUsersPK projHasUsersPK;

    public ProjHasUsers() {
    }

    public ProjHasUsers(ProjHasUsersPK projHasUsersPK) {
        this.projHasUsersPK = projHasUsersPK;
    }

    public ProjHasUsers(String projName, String userName) {
        this.projHasUsersPK = new ProjHasUsersPK(projName, userName);
    }

    public ProjHasUsersPK getProjHasUsersPK() {
        return projHasUsersPK;
    }

    public void setProjHasUsersPK(ProjHasUsersPK projHasUsersPK) {
        this.projHasUsersPK = projHasUsersPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projHasUsersPK != null ? projHasUsersPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjHasUsers)) {
            return false;
        }
        ProjHasUsers other = (ProjHasUsers) object;
        if ((this.projHasUsersPK == null && other.projHasUsersPK != null) || (this.projHasUsersPK != null && !this.projHasUsersPK.equals(other.projHasUsersPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsers[ projHasUsersPK=" + projHasUsersPK + " ]";
    }
    
}
