/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "Projects.findByRedmineEnabled", query = "SELECT p FROM Projects p WHERE p.redmineEnabled = :redmineEnabled")})
public class Projects implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
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
    @JoinTable(name = "proj_has_messages", joinColumns = {
        @JoinColumn(name = "projmsgid", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "msgid", referencedColumnName = "id")})
    @ManyToMany
    private Collection<ProjectMessage> projectMessageCollection;
    @JoinColumn(name = "proj_description", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ProjectDescription projDescription;
    @JoinColumn(name = "pub_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PublicationTypes pubType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projects")
    private Collection<ProjHasUsers> projHasUsersCollection;

    public Projects() {
    }

    public Projects(Integer id) {
        this.id = id;
    }

    public Projects(Integer id, String projName, boolean svnEnabled, boolean gitEnabled, boolean tracEnabled, boolean redmineEnabled) {
        this.id = id;
        this.projName = projName;
        this.svnEnabled = svnEnabled;
        this.gitEnabled = gitEnabled;
        this.tracEnabled = tracEnabled;
        this.redmineEnabled = redmineEnabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public boolean getRedmineEnabled() {
        return redmineEnabled;
    }

    public void setRedmineEnabled(boolean redmineEnabled) {
        this.redmineEnabled = redmineEnabled;
    }

    @XmlTransient
    public Collection<ProjectMessage> getProjectMessageCollection() {
        return projectMessageCollection;
    }

    public void setProjectMessageCollection(Collection<ProjectMessage> projectMessageCollection) {
        this.projectMessageCollection = projectMessageCollection;
    }

    public ProjectDescription getProjDescription() {
        return projDescription;
    }

    public void setProjDescription(ProjectDescription projDescription) {
        this.projDescription = projDescription;
    }

    public PublicationTypes getPubType() {
        return pubType;
    }

    public void setPubType(PublicationTypes pubType) {
        this.pubType = pubType;
    }

    @XmlTransient
    public Collection<ProjHasUsers> getProjHasUsersCollection() {
        return projHasUsersCollection;
    }

    public void setProjHasUsersCollection(Collection<ProjHasUsers> projHasUsersCollection) {
        this.projHasUsersCollection = projHasUsersCollection;
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

    public boolean getIsPublic() {
        return this.getPubType().isPublic();
    }

    public boolean getIsPariatlyPublic() {
        return this.getPubType().isPartialyPublic();
    }

    public boolean getIsPrivate() {
        return this.getPubType().isPrivate();
    }
    
    public boolean getIsHidden() {
        return this.getPubType().isHidden();
    }

    public List<Users> getOwners() {
        List<Users> result = null;
        for (ProjHasUsers project : getProjHasUsersCollection()) {
            if (project.getRola().isAdministrator()) {
                result.add(project.getUsers());
            }
        }
        return result;
    }

    public List<Users> getDevelopers() {
        List<Users> result = null;
        for (ProjHasUsers project : getProjHasUsersCollection()) {
            if (project.getRola().isDeveloper()) {
                result.add(project.getUsers());
            }
        }
        return result;
    }

    public List<Users> getGuests() {
        List<Users> result = null;
        for (ProjHasUsers project : getProjHasUsersCollection()) {
            if (project.getRola().isGuest()) {
                result.add(project.getUsers());
            }
        }
        return result;
    }
}
