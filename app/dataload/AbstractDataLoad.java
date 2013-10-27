package dataload;

import models.*;
import org.xml.sax.SAXException;
import play.Logger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public abstract class AbstractDataLoad {

    protected String dataPath = "app/dataload/data";

    protected Episode createEpisode(String title, Date airtime, int episodeNumber, String description) {

        Episode episode = new Episode();
        episode.setTitle(title);
        episode.setAirtime(airtime);
        episode.setNumber(episodeNumber);
        episode.setDescription(description);

        return episode;

    }

    protected void addSeasonsToShow(Show show, Map<Integer, Map<Integer, Episode>> seasons) {

        List<Season> orderedSeasons = new ArrayList<>();

        for (Map.Entry<Integer, Map<Integer, Episode>> entry : seasons.entrySet()) {

            Season season = new Season();
            season.setNumber(entry.getKey());

            List<Episode> episodes = new ArrayList<>(entry.getValue().values());
            Collections.sort(episodes, new Comparator<Episode>() {
                public int compare(Episode o1, Episode o2) {
                    return o1.getNumber() - o2.getNumber();
                }
            });

            season.setEpisodes(episodes);
            orderedSeasons.add(season);

        }

        Collections.sort(orderedSeasons, new Comparator<Season>() {
            public int compare(Season o1, Season o2) {
                return o1.getNumber() - o2.getNumber();
            }
        });
        show.setSeasons(orderedSeasons);

    }

    /**
     * Initial DataLoad of all shows.
     *
     * @return a list of all shows to import
     */
    public abstract List<Show> loadShows() throws IOException, ParseException, ParserConfigurationException, SAXException;

    public abstract List<Show> loadUpdates() throws IOException, ParseException, ParserConfigurationException, SAXException;

    private Episode findEpisode(List<Episode> episodes, int number) {
        for (Episode episode : episodes) {
            if (episode.getNumber() == number) return episode;
        }
        return null;
    }

    private Season findSeason(List<Season> seasons, int number) {
        for (Season season : seasons) {
            if (season.getNumber() == number) return season;
        }
        return null;
    }

    private boolean containsActor(Set<Actor> actors, Actor actor) {
        for (Actor actorFromSet : actors) {
            if (actorFromSet.getName().equals(actor.getName())) return true;
        }
        return false;
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

    public void saveShows() throws ParserConfigurationException, SAXException, ParseException, IOException {

        List<Show> shows = loadShows();
        insertIntoDatabase(shows);

    }

    public void updateShows() throws ParserConfigurationException, SAXException, ParseException, IOException {

        List<Show> shows = loadUpdates();
        insertIntoDatabase(shows);

    }

}
