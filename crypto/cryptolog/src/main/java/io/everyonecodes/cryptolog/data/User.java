package io.everyonecodes.cryptolog.data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "auth_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_user_id")
    private int id;

    @NotBlank(message = "Name must not be empty")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email is not valid. Please enter a correct email")
    @Column(name = "email", unique = true)
    private String email;

    private String resetToken;

    private int loginAttempts;

    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[!@#\\]\\[:()\\\"`;+\\-'|_?,.</\\\\>=$%}{^&*~]).{8,}$",
    message = "Password needs to be 8 characters in length and must contain at least one lower case letter, one upper case letter, one number and one special character")
    @Column(name = "password")
    private String password;

    @Column(name = "verified")
    private boolean isVerified;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Role> roles;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> coinIds = new HashSet<>();
    
    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Set<String> getCoinIds() {
        return coinIds;
    }


    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && loginAttempts == user.loginAttempts && isVerified == user.isVerified && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(resetToken, user.resetToken) && Objects.equals(password, user.password) && Objects.equals(roles, user.roles);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, resetToken, loginAttempts, password, isVerified, roles);
    }
    
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", resetToken='" + resetToken + '\'' + ", loginAttempts=" + loginAttempts + ", password='" + password + '\'' + ", isVerified=" + isVerified + ", roles=" + roles + '}';
    }
}
