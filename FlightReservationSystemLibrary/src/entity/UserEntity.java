/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Entity
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull
    @Min(2)
    @Max(80)
    @Column(nullable = false, length = 80)
    private String firstName;
    @NotNull
    @Min(2)
    @Max(80)
    @Column(nullable = false, length = 80)
    private String lastName;
    @NotNull
    @Min(6)
    @Max(16)
    @Column(nullable = false, length = 16, unique = true)
    private String loginId;
    @NotNull
    @Min(8)
    @Max(16)
    @Column(nullable = false, length = 16)
    private String loginPw;
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public UserEntity() {
    }

    public UserEntity(String firstName, String lastName, String loginId, String loginPw, UserRole userRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.userRole = userRole;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginPw() {
        return loginPw;
    }

    public void setLoginPw(String loginPw) {
        this.loginPw = loginPw;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getUserId() != null ? getUserId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the userId fields are not set
        if (!(object instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) object;
        if ((this.getUserId() == null && other.getUserId() != null) || (this.getUserId() != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.EmployeeEntity[ id=" + getUserId() + " ]";
    }

}
