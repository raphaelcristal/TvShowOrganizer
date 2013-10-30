package models;

import javax.persistence.Entity;

@Entity
public class Settings {

    private boolean hideDescriptions = false;

    public boolean getHideDescriptions() {
        return hideDescriptions;
    }

    public void setHideDescriptions(boolean hideDescriptions) {
        this.hideDescriptions = hideDescriptions;
    }
}
