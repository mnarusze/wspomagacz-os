/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "publication_types")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PublicationTypes.findAll", query = "SELECT p FROM PublicationTypes p"),
    @NamedQuery(name = "PublicationTypes.findById", query = "SELECT p FROM PublicationTypes p WHERE p.id = :id"),
    @NamedQuery(name = "PublicationTypes.findByTypeName", query = "SELECT p FROM PublicationTypes p WHERE p.typeName = :typeName")})
public class PublicationTypes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "type_name")
    private String typeName;
    @Lob
    @Size(max = 65535)
    @Column(name = "publication_description")
    private String publicationDescription;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pubType")
    private Collection<Projects> projectsCollection;

    public PublicationTypes() {
    }

    public PublicationTypes(Integer id) {
        this.id = id;
    }

    public PublicationTypes(Integer id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isPublic() {
        if (this.getId().equals(1)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPartialyPublic() {
        if (this.getId().equals(2)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPrivate() {
        if (this.getId().equals(3)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isHidden() {
        if (this.getId().equals(4)) {
            return true;
        } else {
            return false;
        }
    }

    public String getPublicationDescription() {
        return publicationDescription;
    }

    public void setPublicationDescription(String publicationDescription) {
        this.publicationDescription = publicationDescription;
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
        if (!(object instanceof PublicationTypes)) {
            return false;
        }
        PublicationTypes other = (PublicationTypes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.PublicationTypes[ id=" + id + " ]";
    }
}
