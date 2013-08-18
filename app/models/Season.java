package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "seasons", uniqueConstraints = @UniqueConstraint(columnNames={"number", "show_id"}))
public class Season extends Model {

    @Id
    private Long id;

    @Constraints.Required
    private int number;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Episode> episodes;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public static Finder<Long, Season> find = new Finder<>(Long.class, Season.class);

}