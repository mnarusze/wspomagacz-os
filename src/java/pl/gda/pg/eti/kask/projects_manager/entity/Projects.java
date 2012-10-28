/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mateusz
 */
@Entity
@Table(name = "projects")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Projects.findAll", query = "SELECT p FROM Projects p"),
    @NamedQuery(name = "Projects.findByProjName", query = "SELECT p FROM Projects p WHERE p.projName = :projName"),
    @NamedQuery(name = "Projects.findByGid", query = "SELECT p FROM Projects p WHERE p.gid = :gid"),
    @NamedQuery(name = "Projects.findByOwnerUid", query = "SELECT p FROM Projects p WHERE p.ownerUid = :ownerUid"),
    @NamedQuery(name = "Projects.findBySvnEnabled", query = "SELECT p FROM Projects p WHERE p.svnEnabled = :svnEnabled"),
    @NamedQuery(name = "Projects.findByGitEnabled", query = "SELECT p FROM Projects p WHERE p.gitEnabled = :gitEnabled"),
    @NamedQuery(name = "Projects.findByTracEnabled", query = "SELECT p FROM Projects p WHERE p.tracEnabled = :tracEnabled"),
    @NamedQuery(name = "Projects.findByIsPublic", query = "SELECT p FROM Projects p WHERE p.isPublic = :isPublic")})
public class Projects implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "proj_name")
    private String projName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "gid")
    private int gid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "owner_uid")
    private int ownerUid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "svn_enabled")
    private boolean svnEnabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "git_enabled")
    private boolean gitEnabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "trac_enabled")
    private boolean tracEnabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_public")
    private boolean isPublic;

    public Projects() {
    }

    public Projects(String projName) {
        this.projName = projName;
    }

    public Projects(String projName, int gid, int ownerUid, boolean svnEnabled, boolean gitEnabled, boolean tracEnabled, boolean isPublic) {
        this.projName = projName;
        this.gid = gid;
        this.ownerUid = ownerUid;
        this.svnEnabled = svnEnabled;
        this.gitEnabled = gitEnabled;
        this.tracEnabled = tracEnabled;
        this.isPublic = isPublic;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(int ownerUid) {
        this.ownerUid = ownerUid;
    }

    public boolean getSvnEnabled() {
        return svnEnabled;
    }

    public void setSvnEnabled(boolean svnEnabled) {
        this.svnEnabled = svnEnabled;
    }

    public boolean getGitEnabled() {
        return gitEnabled;
    }

    public void setGitEnabled(boolean gitEnabled) {
        this.gitEnabled = gitEnabled;
    }

    public boolean getTracEnabled() {
        return tracEnabled;
    }

    public void setTracEnabled(boolean tracEnabled) {
        this.tracEnabled = tracEnabled;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projName != null ? projName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Projects)) {
            return false;
        }
        Projects other = (Projects) object;
        if ((this.projName == null && other.projName != null) || (this.projName != null && !this.projName.equals(other.projName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.Projects[ projName=" + projName + " ]";
    }
    
}
