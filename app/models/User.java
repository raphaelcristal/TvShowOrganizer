package models;

import com.avaje.ebean.validation.Email;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Set;

@Entity
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

    @Constraints.Required
    @OneToOne(cascade = {CascadeType.ALL})
    @JsonIgnore
    private AuthToken authToken;

    private Settings settings = new Settings();

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

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }


    public static Finder<Long, User> find = new Finder<>(Long.class, User.class);

}