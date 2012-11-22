/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Mateusz
 */
@Entity
@Table(name = "projects")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Projects.findAll", query = "SELECT p FROM Projects p"),
    @NamedQuery(name = "Projects.findById", query = "SELECT p FROM Projects p WHERE p.id = :id"),
    @NamedQuery(name = "Projects.findByProjName", query = "SELECT p FROM Projects p WHERE p.projName = :projName"),
    @NamedQuery(name = "Projects.findBySvnEnabled", query = "SELECT p FROM Projects p WHERE p.svnEnabled = :svnEnabled"),
    @NamedQuery(name = "Projects.findByGitEnabled", query = "SELECT p FROM Projects p WHERE p.gitEnabled = :gitEnabled"),
    @NamedQuery(name = "Projects.findByTracEnabled", query = "SELECT p FROM Projects p WHERE p.tracEnabled = :tracEnabled"),
    @NamedQuery(name = "Projects.findByRedmineEnabled", query = "SELECT p FROM Projects p WHERE p.tracEnabled = :tracEnabled"),
    @NamedQuery(name = "Projects.findByIsPublic", query = "SELECT p FROM Projects p WHERE p.isPublic = :isPublic")})
public class Projects implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Short id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "proj_name")
    private String projName;
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
    @Column(name = "redmine_enabled")
    private boolean redmineEnabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_public")
    private boolean isPublic;
    @JoinTable(name = "proj_has_users", joinColumns = {
        @JoinColumn(name = "projid", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "userid", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Users> usersCollection;
    @JoinColumn(name = "owner", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users owner;

    public Projects() {
    }

    public Projects(Short id) {
        this.id = id;
    }

    public Projects(Short id, String projName, boolean svnEnabled, boolean gitEnabled, boolean tracEnabled, boolean isPublic) {
        this.id = id;
        this.projName = projName;
        this.svnEnabled = svnEnabled;
        this.gitEnabled = gitEnabled;
        this.tracEnabled = tracEnabled;
        this.isPublic = isPublic;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
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

    public boolean isRedmineEnabled() {
        return redmineEnabled;
    }

    public void setRedmineEnabled(boolean redmineEnabled) {
        this.redmineEnabled = redmineEnabled;
    }

    
    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @XmlTransient
    public Collection<Users> getUsersCollection() {
        return usersCollection;
    }

    public void setUsersCollection(Collection<Users> usersCollection) {
        this.usersCollection = usersCollection;
    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Projects)) {
            return false;
        }
        Projects other = (Projects) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.Projects[ id=" + id + " ]";
    }
    
}
