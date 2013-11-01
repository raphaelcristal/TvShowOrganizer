package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Settings {

    @Id
    private Long id;

    private boolean hideDescriptions = false;
    private int passedDaysToShow = 7;

    public boolean getHideDescriptions() {
        return hideDescriptions;
    }

    public void setHideDescriptions(boolean hideDescriptions) {
        this.hideDescriptions = hideDescriptions;
    }

    public int getPassedDaysToShow() {
        return passedDaysToShow;
    }

    public void setPassedDaysToShow(int passedDaysToShow) {
        this.passedDaysToShow = passedDaysToShow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
