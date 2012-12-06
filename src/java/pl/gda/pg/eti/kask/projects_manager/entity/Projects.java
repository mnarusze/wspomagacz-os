/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Mateusz
 */
@Entity
@Table(name = "projects")
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
    @JoinColumn(name = "proj_description", referencedColumnName = "id")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private ProjectDescription projDescription;
    @JoinColumn(name = "pub_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PublicationTypes pubType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projects")
    private Collection<ProjHasUsers> projHasUsersCollection;
    private final static int PUBLIC = 1;
    private final static int PARTIALY_PUBLIC = 2;
    private final static int PRIVATE = 3;
    private final static int HIDDEN = 4;
    private final static int ADMINISTRATOR = 1;
    private final static int DEVELOPER = 2;
    private final static int GUEST = 3;

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
        return this.pubType.getId().equals(PUBLIC);
    }

    public boolean getIsPariatlyPublic() {
        return this.pubType.getId().equals(PARTIALY_PUBLIC);
    }
    
    public boolean getIsPrivate() {
        return this.pubType.getId().equals(PRIVATE);
    }
    
    public boolean getIsHidden() {
        return this.pubType.getId().equals(HIDDEN);
    }

    public void addUserPerRole(Users owner, int i) {
        ProjHasUsers ph;
        ph = new ProjHasUsers();
        ph.setProjHasUsersPK(new ProjHasUsersPK(this.id, owner.getId()));
        ph.setProjects(this);
        ph.setUsers(owner);

        ph.setRola(new UserRoles(i));

        ProjHasUsers existing = null;

        for (ProjHasUsers projHasUsers : projHasUsersCollection) {
            if (projHasUsers.getUsers().getLogin().equals(owner.getLogin())) {
                existing = projHasUsers;
            } else {
            }
        }
        if (existing != null) {
            this.projHasUsersCollection.remove(existing);
        } else {
        }
        this.projHasUsersCollection.add(ph);
    }

    public List<Users> getUserPerRole(int role) {
        List<Users> result;
        result = new ArrayList<Users>();
        try {
            for (ProjHasUsers users : this.projHasUsersCollection) {
                if (users.getRola().getId().equals(role)) {
                    result.add(users.getUsers());
                } else {
                }
            }
        } catch (Exception e) {
        }

        return result;
    }

    public List<Users> getGuests() {
        return getUserPerRole(GUEST);
    }

    public List<Users> getDevelopers() {
        return getUserPerRole(DEVELOPER);
    }

    public List<Users> getOwners() {
        return getUserPerRole(ADMINISTRATOR);
    }

    public void removeUserFromProject(Users u) {
        
        ProjHasUsers existing = null;

        for (ProjHasUsers projHasUsers : projHasUsersCollection) {
            if (projHasUsers.getUsers().getLogin().equals(u.getLogin())) {
                existing = projHasUsers;
            } else {
            }
        }
        if (existing != null) {
            this.projHasUsersCollection.remove(existing);
        } else {
        }
        
    }
}
