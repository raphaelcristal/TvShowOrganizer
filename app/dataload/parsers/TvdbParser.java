package dataload.parsers;

import dataload.provider.TvdbProvider;
import models.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TvdbParser extends AbstractShowParser {

    private final TvdbProvider tvdbProvider;

    public TvdbParser(TvdbProvider tvdbProvider) {
        this.tvdbProvider = tvdbProvider;
    }

    @Override
    public List<Show> parseShows() throws IOException, ParseException, ParserConfigurationException, SAXException {

        List<Show> shows = new ArrayList<>();

        for (Show show : Show.find.where().isNotNull("tvdbId").findSet()) {

            Element root = tvdbProvider.fetchShow(show.getTvdbId()).getDocumentElement();
            show = parseShow(show, root);

            shows.add(show);

        }

        return shows;

    }

    private Show parseShow(Show show, Element root) {

        String showDescription = root.getElementsByTagName("Overview").item(0).getTextContent();
        Day airday = Day.parseDay(root.getElementsByTagName("Airs_DayOfWeek").item(0).getTextContent());
        String airtime = root.getElementsByTagName("Airs_Time").item(0).getTextContent();
        show.setDescription(showDescription.replace("\n", ""));
        show.setAirday(airday);
        show.setAirtime(airtime);

        Network network = parseNetwork(root);
        if (network != null) {
            show.setNetwork(network);
        }

        Set<Actor> actors = parseActors(root);
        show.setActors(actors);

        List<Season> seasons = parseSeasons(root);
        show.setSeasons(seasons);

        return show;

    }

    private Network parseNetwork(Element root) {

        String networkName = root.getElementsByTagName("Network").item(0).getTextContent().trim();
        if (!networkName.isEmpty()) {
            Network network = new Network();
            network.setName(networkName);
            return network;
        }
        return null;

    }

    private Set<Actor> parseActors(Element root) {

        Set<Actor> parsedActors = new HashSet<>();
        String[] actors = root.getElementsByTagName("Actors").item(0).getTextContent().split("\\|");
        for (String actorName : actors) {
            actorName = actorName.trim();
            if (!actorName.isEmpty()) {
                Actor actor = new Actor();
                actor.setName(actorName);
                parsedActors.add(actor);
            }
        }

        return parsedActors;

    }

    private List<Season> parseSeasons(Element root) {

        Map<Integer, Map<Integer, Episode>> seasons = new HashMap<>();

        NodeList episodes = root.getElementsByTagName("Episode");
        for (int i = 0; i < episodes.getLength(); i++) {

            Element episodeElement = (Element) episodes.item(i);

            int seasonNumber = Integer.parseInt(episodeElement.getElementsByTagName("SeasonNumber").item(0).getTextContent());
            if (seasonNumber == 0) {
                //seasons < 1 are extra content, ignore it
                continue;
            }

            Episode episode = this.parseEpisode(episodeElement);

            if (!seasons.containsKey(seasonNumber)) {
                seasons.put(seasonNumber, new HashMap<Integer, Episode>());
            }

            seasons.get(seasonNumber).put(episode.getNumber(), episode);

        }

        return orderedSeasonList(seasons);

    }

    private Episode parseEpisode(Element episodeElement) {

        int episodeNumber = Integer.parseInt(episodeElement.getElementsByTagName("EpisodeNumber").item(0).getTextContent());
        String episodeName = episodeElement.getElementsByTagName("EpisodeName").item(0).getTextContent();
        String airTime = episodeElement.getElementsByTagName("FirstAired").item(0).getTextContent();

        Date airTimeParsed;
        try {
            airTimeParsed = new SimpleDateFormat("yyyy-MM-dd").parse(airTime);
        } catch (Exception e) {
            airTimeParsed = null;
        }
        String description = episodeElement.getElementsByTagName("Overview").item(0).getTextContent();

        Episode episode = createEpisode(episodeName, airTimeParsed, episodeNumber, description.replace("\n", ""));

        return episode;

    }

}
