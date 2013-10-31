package models;

import javax.persistence.Entity;

@Entity
public class Settings {

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
}
