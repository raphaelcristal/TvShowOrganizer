package dataload.parsers;

import models.Episode;
import models.Season;
import models.Show;

import java.util.*;

public abstract class AbstractShowParser {

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

}
