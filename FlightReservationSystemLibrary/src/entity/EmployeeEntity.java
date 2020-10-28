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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Entity
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @NotNull
    @Size(min = 2, max = 80, message = "Name should be at least 2 characters long!")
    @Column(nullable = false, length = 80)
    private String name;
    
    @NotNull
    @Size(min = 6, max = 16, message = "Login ID should be between 6 to 16 characters")
    @Column(nullable = false, length = 16, unique = true)
    private String loginId;
    
    @NotNull
    @Size(min = 8, max = 16, message = "Login Password has to be minimum 8 characters and maximum 16")
    @Column(nullable = false, length = 16)
    private String loginPw;
    
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public EmployeeEntity() {
    }

    public EmployeeEntity(String name, String loginId, String loginPw, UserRole userRole) {
        this.name = name;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.userRole = userRole;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        hash += (getEmployeeId() != null ? getEmployeeId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof EmployeeEntity)) {
            return false;
        }
        EmployeeEntity other = (EmployeeEntity) object;
        if ((this.getEmployeeId() == null && other.getEmployeeId() != null) || (this.getEmployeeId() != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.EmployeeEntity[ id=" + getEmployeeId() + " ]";
    }

}
