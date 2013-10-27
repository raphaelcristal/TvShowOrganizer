package dataload.parsers;

import models.Episode;
import models.Season;
import models.Show;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public abstract class AbstractShowParser {

    public abstract List<Show> parseShows() throws IOException, ParseException, ParserConfigurationException, SAXException;

    public abstract List<Show> parseUpdates() throws Exception;

    protected Episode createEpisode(String title, Date airtime, int episodeNumber, String description) {

        Episode episode = new Episode();
        episode.setTitle(title);
        episode.setAirtime(airtime);
        episode.setNumber(episodeNumber);
        episode.setDescription(description);

        return episode;

    }

    protected List<Season> orderedSeasonList(Map<Integer, Map<Integer, Episode>> seasons) {

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

        return orderedSeasons;

    }

}
