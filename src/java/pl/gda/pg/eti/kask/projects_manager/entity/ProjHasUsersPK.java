/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Mateusz
 */
@Embeddable
public class ProjHasUsersPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "proj_name")
    private String projName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "user_name")
    private String userName;

    public ProjHasUsersPK() {
    }

    public ProjHasUsersPK(String projName, String userName) {
        this.projName = projName;
        this.userName = userName;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projName != null ? projName.hashCode() : 0);
        hash += (userName != null ? userName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjHasUsersPK)) {
            return false;
        }
        ProjHasUsersPK other = (ProjHasUsersPK) object;
        if ((this.projName == null && other.projName != null) || (this.projName != null && !this.projName.equals(other.projName))) {
            return false;
        }
        if ((this.userName == null && other.userName != null) || (this.userName != null && !this.userName.equals(other.userName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsersPK[ projName=" + projName + ", userName=" + userName + " ]";
    }
    
}
