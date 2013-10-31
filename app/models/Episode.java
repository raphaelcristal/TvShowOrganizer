package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"number", "season_id"}))
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Episode episode = (Episode) o;

        if (id != null ? !id.equals(episode.id) : episode.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}