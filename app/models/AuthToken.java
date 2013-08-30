package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import org.codehaus.jackson.annotate.JsonIgnore;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "authTokens")
public class AuthToken extends Model {

    @Id
    @JsonIgnore
    private Long id;

    @Constraints.Required
    @Column(unique = true)
    private String token;

    @Constraints.Required
    @CreatedTimestamp
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    private Date creationDate;

    @Constraints.Required
    private boolean active = true;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public static Finder<Long, AuthToken> find = new Finder<>(Long.class, AuthToken.class);

}
