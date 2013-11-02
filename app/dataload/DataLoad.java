package dataload;

import dataload.parsers.AbstractShowParser;
import models.*;
import org.xml.sax.SAXException;
import play.Logger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataLoad {

    private final List<AbstractShowParser> showParsers;

    /**
     * Beware of the order of your showParser. A following parser will override the first parser's values
     * if the information is not null (eg airTime, description, etc). So your best source should come last in the list,
     * and your worst first!
     *
     * @param showParsers
     */
    public DataLoad(List<AbstractShowParser> showParsers) {

        this.showParsers = showParsers;

    }

    /**
     * Use this constructor if you don't want to load from an external ressource but supply
     * the show objects yourself
     */
    public DataLoad() {
        this(null);
    }

    public void importShows(List<Show> shows) {
        insertIntoDatabase(shows);
    }

    /**
     * Run this method to start the initial dataload on an empty database.
     */
    public void importShows() {

        for (AbstractShowParser showParser : showParsers) {

            try {
                List<Show> shows = showParser.parseShows();
                insertIntoDatabase(shows);
            } catch (IOException e) {
                Logger.error("Error while trying to import shows with parser: " + showParser.getClass().getName(), e);
            } catch (ParseException | ParserConfigurationException | SAXException e) {
                Logger.error("Error while trying to parse shows with parser: " + showParser.getClass().getName(), e);
            }

        }

    }

    public void updateShows() {

        for (AbstractShowParser showParser : showParsers) {

            try {
                List<Show> shows = showParser.parseUpdates();
                insertIntoDatabase(shows);
            } catch (Exception e) {
                Logger.error("Error while trying to update shows with parser: " + showParser.getClass().getName(), e);
            }

        }

    }

    private void insertIntoDatabase(List<Show> shows) {

        for (Show show : shows) {

            Show savedShow = Show.find.where().eq("title", show.getTitle()).findUnique();

            if (savedShow != null) {
                updateShow(savedShow, show);
                Logger.info("Updating show: " + savedShow.getTitle());
                try {
                    savedShow.save();
                } catch (Exception e) {
                    Logger.error("Could not update show: " + savedShow.getTitle());
                    Logger.error(e.toString());
                }
            } else {
                try {
                    Logger.info("Saving show: " + show.getTitle());
                    show.setActors(createOrLoadActors(show.getActors()));
                    if (show.getNetwork() != null) {
                        show.setNetwork(createOrLoadNetwork(show.getNetwork()));
                    }
                    show.save();
                } catch (Exception e) {
                    Logger.error("Could not save show: " + show.getTitle());
                    Logger.error(e.toString());
                }
            }
        }

    }

    private void updateShow(Show savedShow, Show newShow) {

        for (Actor newActor : createOrLoadActors(newShow.getActors())) {
            if (!containsActor(savedShow.getActors(), newActor)) {
                savedShow.getActors().add(newActor);
            }
        }
        if (newShow.getAirday() != null) savedShow.setAirday(newShow.getAirday());
        if (newShow.getAirtime() != null) savedShow.setAirtime(newShow.getAirtime());
        if (newShow.getDescription() != null) savedShow.setDescription(newShow.getDescription());
        if (newShow.getTvdbId() != null) savedShow.setTvdbId(newShow.getTvdbId());
        if (newShow.getNetwork() != null) {
            savedShow.setNetwork(createOrLoadNetwork(newShow.getNetwork()));
        }
        for (Season season : newShow.getSeasons()) {
            Season savedSeason = findSeason(savedShow.getSeasons(), season.getNumber());
            if (savedSeason == null) {
                savedShow.getSeasons().add(season);
            } else {
                for (Episode episode : season.getEpisodes()) {
                    Episode savedEpisode = findEpisode(savedSeason.getEpisodes(), episode.getNumber());
                    if (savedEpisode == null) {
                        savedSeason.getEpisodes().add(episode);
                    } else {
                        if (episode.getAirtime() != null) savedEpisode.setAirtime(episode.getAirtime());
                        if (episode.getDescription() != null) savedEpisode.setDescription(episode.getDescription());
                        if (episode.getTitle() != null) savedEpisode.setTitle(episode.getTitle());
                    }
                }
            }

        }

    }

    private Set<Actor> createOrLoadActors(Set<Actor> actors) {

        Set<Actor> newActors = new HashSet<>();

        for (Actor actor : actors) {
            Actor savedActor = Actor.find.where().eq("name", actor.getName()).findUnique();
            if (savedActor == null) {
                actor.save();
                newActors.add(actor);
            } else {
                newActors.add(savedActor);
            }
        }

        return newActors;
    }

    private Network createOrLoadNetwork(Network network) {
        Network savedNetwork = Network.find.where().eq("name", network.getName()).findUnique();
        if (savedNetwork == null) {
            network.save();
            return network;
        }
        return savedNetwork;
    }

    private boolean containsActor(Set<Actor> actors, Actor actor) {
        for (Actor actorFromSet : actors) {
            if (actorFromSet.getName().equals(actor.getName())) return true;
        }
        return false;
    }

    private Season findSeason(List<Season> seasons, int number) {
        for (Season season : seasons) {
            if (season.getNumber() == number) return season;
        }
        return null;
    }

    private Episode findEpisode(List<Episode> episodes, int number) {
        for (Episode episode : episodes) {
            if (episode.getNumber() == number) return episode;
        }
        return null;
    }
}
