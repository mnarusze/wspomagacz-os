/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    @NamedQuery(name = "ProjHasUsers.findByProjid", query = "SELECT p FROM ProjHasUsers p WHERE p.projHasUsersPK.projid = :projid"),
    @NamedQuery(name = "ProjHasUsers.findByUserid", query = "SELECT p FROM ProjHasUsers p WHERE p.projHasUsersPK.userid = :userid")})
public class ProjHasUsers implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProjHasUsersPK projHasUsersPK;
    @JoinColumn(name = "userid", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Users users;
    @JoinColumn(name = "projid", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Projects projects;
    @JoinColumn(name = "rola", referencedColumnName = "id")
    @ManyToOne
    private UserRoles rola;

    public ProjHasUsers() {
    }

    public ProjHasUsers(ProjHasUsersPK projHasUsersPK) {
        this.projHasUsersPK = projHasUsersPK;
    }

    public ProjHasUsers(int projid, int userid) {
        this.projHasUsersPK = new ProjHasUsersPK(projid, userid);
    }

    public ProjHasUsersPK getProjHasUsersPK() {
        return projHasUsersPK;
    }

    public void setProjHasUsersPK(ProjHasUsersPK projHasUsersPK) {
        this.projHasUsersPK = projHasUsersPK;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Projects getProjects() {
        return projects;
    }

    public void setProjects(Projects projects) {
        this.projects = projects;
    }

    public UserRoles getRola() {
        return rola;
    }

    public void setRola(UserRoles rola) {
        this.rola = rola;
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
