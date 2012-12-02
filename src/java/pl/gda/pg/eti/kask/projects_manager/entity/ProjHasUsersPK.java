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

/**
 *
 * @author Mateusz
 */
@Embeddable
public class ProjHasUsersPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "projid")
    private int projid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "userid")
    private int userid;

    public ProjHasUsersPK() {
    }

    public ProjHasUsersPK(int projid, int userid) {
        this.projid = projid;
        this.userid = userid;
    }

    public int getProjid() {
        return projid;
    }

    public void setProjid(int projid) {
        this.projid = projid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) projid;
        hash += (int) userid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjHasUsersPK)) {
            return false;
        }
        ProjHasUsersPK other = (ProjHasUsersPK) object;
        if (this.projid != other.projid) {
            return false;
        }
        if (this.userid != other.userid) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsersPK[ projid=" + projid + ", userid=" + userid + " ]";
    }
    
}
