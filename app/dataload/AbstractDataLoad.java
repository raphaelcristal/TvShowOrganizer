package dataload;

import com.avaje.ebean.Ebean;
import models.Episode;
import models.Season;
import models.Show;
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
    abstract List<Show> loadShows() throws IOException, ParseException, ParserConfigurationException, SAXException;

    public void saveShows() throws IOException, ParseException, ParserConfigurationException, SAXException {

        List<Show> shows = loadShows();

        for (Show show : shows) {

            Logger.info("Saving show: " + show.getTitle());
            try {
                show.save();
            } catch (Exception e) {
                Logger.error("Could not save show: " + show.getTitle());
                Logger.error(e.toString());
            }
        }

    }

}
