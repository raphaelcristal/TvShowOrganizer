package dataload.parsers;

import com.google.common.collect.ImmutableList;
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
    private final boolean fromFilesOnly;

    public TvdbParser(TvdbProvider tvdbProvider, boolean fromFilesOnly) {
        this.tvdbProvider = tvdbProvider;
        this.fromFilesOnly = fromFilesOnly;
    }

    @Override
    public List<Show> parseUpdates() throws Exception {

        List<Show> shows = new ArrayList<>();

        Element root = tvdbProvider.fetchUpdates().getDocumentElement();

        for (Show show : fetchShowsToUpdate(root)) {

            Element showXml = tvdbProvider.fetchShow(show.getTvdbId()).getDocumentElement();
            Show updatedShow = parseShow(show, showXml);
            shows.add(updatedShow);

        }

        List<Show> episodeUpdates = fetchEpisodesToUpdate(root);
        shows.addAll(episodeUpdates);

        return shows;
    }

    private List<Show> fetchShowsToUpdate(Element root) {

        List<Show> shows = new ArrayList<>();

        NodeList series = root.getElementsByTagName("Series");
        for (int i = 0; i < series.getLength(); i++) {
            Element serie = (Element) series.item(i);
            if (serie.getParentNode().getNodeName().equals("Data")) {
                Integer id = Integer.valueOf(serie.getElementsByTagName("id").item(0).getTextContent());
                Show show = Show.find.where().eq("tvdbId", id).findUnique();
                if (show != null) {
                    shows.add(show);
                }
            }
        }

        return shows;

    }

    private List<Show> fetchEpisodesToUpdate(Element root) throws Exception {

        List<Show> shows = new ArrayList<>();

        NodeList episodes = root.getElementsByTagName("Episode");
        for (int i = 0; i < episodes.getLength(); i++) {

            Element episode = (Element) episodes.item(i);
            String episodeId = episode.getElementsByTagName("id").item(0).getTextContent();
            String showId = episode.getElementsByTagName("Series").item(0).getTextContent();

            Show show = Show.find.where().eq("tvdbId", showId).findUnique();
            if (show != null) {
                Element episodeXml = tvdbProvider.fetchEpisode(Integer.valueOf(episodeId)).getDocumentElement();
                int seasonNumber = Integer.parseInt(episodeXml.getElementsByTagName("SeasonNumber").item(0).getTextContent());

                Season season = new Season();
                season.setNumber(seasonNumber);
                season.setEpisodes(ImmutableList.of(parseEpisode(episodeXml)));

                show.setSeasons(ImmutableList.of(season));

                shows.add(show);

            }
        }

        return shows;
    }

    @Override
    public List<Show> parseShows() throws IOException, ParseException, ParserConfigurationException, SAXException {

        List<Show> shows = new ArrayList<>();

        for (Show show : Show.find.all()) {

            String title = show.getTitle();
            int tvdbId = fromFilesOnly ? tvdbProvider.getIdByNameFromFile(title) : tvdbProvider.getIdByNameFromApi(title);

            if (tvdbId != -1) {
                Element root = tvdbProvider.fetchShow(tvdbId).getDocumentElement();
                show = parseShow(show, root);

                shows.add(show);
            }

        }

        tvdbProvider.saveTitleIdMappingToFile();

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
