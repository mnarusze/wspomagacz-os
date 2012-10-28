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
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByNickname", query = "SELECT u FROM Users u WHERE u.nickname = :nickname"),
    @NamedQuery(name = "Users.findByUid", query = "SELECT u FROM Users u WHERE u.uid = :uid"),
    @NamedQuery(name = "Users.findByFirstname", query = "SELECT u FROM Users u WHERE u.firstname = :firstname"),
    @NamedQuery(name = "Users.findByLastname", query = "SELECT u FROM Users u WHERE u.lastname = :lastname"),
    @NamedQuery(name = "Users.findByIndeks", query = "SELECT u FROM Users u WHERE u.indeks = :indeks"),
    @NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email")})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "nickname")
    private String nickname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "uid")
    private int uid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "firstname")
    private String firstname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "lastname")
    private String lastname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "indeks")
    private int indeks;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "email")
    private String email;

    public Users() {
    }

    public Users(String nickname) {
        this.nickname = nickname;
    }

    public Users(String nickname, int uid, String firstname, String lastname, int indeks, String email) {
        this.nickname = nickname;
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.indeks = indeks;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getIndeks() {
        return indeks;
    }

    public void setIndeks(int indeks) {
        this.indeks = indeks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nickname != null ? nickname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.nickname == null && other.nickname != null) || (this.nickname != null && !this.nickname.equals(other.nickname))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.gda.pg.eti.kask.projects_manager.entity.Users[ nickname=" + nickname + " ]";
    }
    
}
