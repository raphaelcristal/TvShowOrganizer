package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "episodes", uniqueConstraints = @UniqueConstraint(columnNames={"number", "season_id"}))
public class Episode extends Model {

    @Id
    private Long id;

    private String title;

    @Constraints.Required
    private int number;

    @Column(length = 65535, columnDefinition = "Text")
    private String description;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    private Date airtime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getAirtime() {
        return airtime;
    }

    public void setAirtime(Date airtime) {
        this.airtime = airtime;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public static Finder<Long, Episode> find = new Finder<>(Long.class, Episode.class);

}