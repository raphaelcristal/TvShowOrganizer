package models;

import com.avaje.ebean.validation.Email;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends Model {

    @Id
    private Long id;

    @Constraints.Required
    @Column(unique = true, length = 120)
    @Email
    private String email;

    @Constraints.Required
    @JsonIgnore
    private String password;

    @OneToMany(cascade = {CascadeType.ALL})
    @JsonIgnore
    private List<AuthToken> authTokens;

    @ManyToMany(cascade = {CascadeType.ALL})
    private Set<Show> shows;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Set<Show> getShows() {
        return shows;
    }

    public void setShows(Set<Show> shows) {
        this.shows = shows;
    }

    public List<AuthToken> getAuthTokens() {
        return authTokens;
    }

    public void setAuthTokens(List<AuthToken> authTokens) {
        this.authTokens = authTokens;
    }


    public static Finder<Long, User> find = new Finder<>(Long.class, User.class);


}