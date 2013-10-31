package models;

import org.codehaus.jackson.annotate.JsonIgnore;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "shows")
public class Show extends Model {

    @Id
    private Long id;

    @Column(unique = true)
    @JsonIgnore
    private Integer tvdbId = null;

    @Constraints.Required
    @Column(unique = true, length = 180)
    private String title;

    @Column(length = 65535, columnDefinition = "Text")
    private String description;

    private Day airday;

    private String airtime;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Season> seasons;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private Network network;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private Set<Actor> actors;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTvdbId() {
        return this.tvdbId;
    }

    public void setTvdbId(Integer tvdbId) {
        this.tvdbId = tvdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public Day getAirday() {
        return airday;
    }

    public void setAirday(Day airday) {
        this.airday = airday;
    }

    public String getAirtime() {
        return airtime;
    }

    public void setAirtime(String airtime) {
        this.airtime = airtime;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }


    public static Finder<Long, Show> find = new Finder<>(Long.class, Show.class);

}