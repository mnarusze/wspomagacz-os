/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Mateusz
 */
@Entity
@Table(name = "project_description")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjectDescription.findAll", query = "SELECT p FROM ProjectDescription p"),
    @NamedQuery(name = "ProjectDescription.findById", query = "SELECT p FROM ProjectDescription p WHERE p.id = :id"),
    @NamedQuery(name = "ProjectDescription.findByProjFullName", query = "SELECT p FROM ProjectDescription p WHERE p.projFullName = :projFullName"),
    @NamedQuery(name = "ProjectDescription.findByCreationDate", query = "SELECT p FROM ProjectDescription p WHERE p.creationDate = :creationDate"),
    @NamedQuery(name = "ProjectDescription.findByLastChange", query = "SELECT p FROM ProjectDescription p WHERE p.lastChange = :lastChange"),
    @NamedQuery(name = "ProjectDescription.findByProjLogo", query = "SELECT p FROM ProjectDescription p WHERE p.projLogo = :projLogo")})
public class ProjectDescription implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "proj_full_name")
    private String projFullName;
    @Lob
    @Size(max = 65535)
    @Column(name = "proj_description")
    private String projDescription;
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "last_change")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChange;
    @Size(max = 255)
    @Column(name = "proj_logo")
    private String projLogo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projDescription")
    private Collection<Projects> projectsCollection;

    public ProjectDescription() {
    }

    public ProjectDescription(String fullname) {
        this.projFullName = fullname;
    }

    public ProjectDescription(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjFullName() {
        return projFullName;
    }

    public void setProjFullName(String projFullName) {
        this.projFullName = projFullName;
    }

    public String getProjDescription() {
        return projDescription;
    }

    public void setProjDescription(String projDescription) {
        this.projDescription = projDescription;
    }

    public String getShortProjDescription() {
        if (projDescription != null) {
            if(projDescription.length() > 140){
                return projDescription.substring(0, 140);
            } else
            {
                return projDescription;
            }
        }
        return "brak opisu";
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public String getProjLogo() {
        return projLogo;
    }

    public void setProjLogo(String projLogo) {
        this.projLogo = projLogo;
    }

    @XmlTransient
    public Collection<Projects> getProjectsCollection() {
        return projectsCollection;
    }

    public void setProjectsCollection(Collection<Projects> projectsCollection) {
        this.projectsCollection = projectsCollection;
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
        if (!(object instanceof ProjectDescription)) {
            return false;
        }
        ProjectDescription other = (ProjectDescription) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.ProjectDescription[ id=" + id + " ]";
    }
}
