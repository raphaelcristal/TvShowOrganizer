package dataload;

import com.avaje.ebean.Ebean;
import models.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.Logger;
import play.Play;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TvdbDataLoad extends AbstractDataLoad {

    private final static String TVDB_TOKEN = Play.application().configuration().getString("tvdb.token");

    private void downloadXmlFile(Path xmlFile, Integer id) throws IOException {

        Logger.debug("download info for show with tvdbid: " + id);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://thetvdb.com/api/" + TVDB_TOKEN + "/series/" + id + "/all/en.xml");
        HttpResponse response = client.execute(request);


        //getStatusLine for Response Code
        if (response.getStatusLine().getStatusCode() != 200) {
            Logger.warn("Could not download information for show with tvdbid: " + id);
            return;
        }

        Path file = Files.createFile(xmlFile);
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file.toString()));

        response.getEntity().writeTo(stream);


    }

    /**
     * Saves tvdbids for existing shows.
     *
     * @param loadFromFilesOnly only load ids from a the property file tvdbids.properties
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private void saveTvdbIds(boolean loadFromFilesOnly) throws IOException, ParserConfigurationException, SAXException {


        Properties nameIdMapping = new Properties();
        try {
            nameIdMapping.load(new FileInputStream("tvdbids.properties"));
        } catch (Exception e) {
            Logger.info("Could not load id to name mapping file, creating a new one");
        }

        for (Show savedShow : Show.find.where().isNull("tvdbId").findList()) {

            if (nameIdMapping.containsKey(savedShow.getTitle())) {

                try {
                    savedShow.setTvdbId(Integer.valueOf(nameIdMapping.getProperty(savedShow.getTitle())));
                    savedShow.save();
                    Logger.info("Saving TvdbId from file for show: " + savedShow.getTitle());
                } catch (Exception e) {
                    Logger.error("Could not inser TvdbId for show: " + savedShow.getTitle());
                } finally {
                    //since we already found our show, don't search any further
                    continue;
                }

            }

            //don't call the api if we want to load from our local files only
            if (loadFromFilesOnly) {
                continue;
            }

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://thetvdb.com/api/GetSeries.php?seriesname=" + URLEncoder.encode(savedShow.getTitle(), "UTF-8"));
            HttpResponse response = client.execute(request);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            response.getEntity().writeTo(output);

            Element root = null;
            try {
                root = DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(new ByteArrayInputStream(output.toByteArray()))
                        .getDocumentElement();
            } catch (Exception e) {

                Logger.error("Could not parse xml for Show:" + savedShow.getTitle());
                continue;

            }

            NodeList series = root.getElementsByTagName("Series");
            for (int i = 0; i < series.getLength(); i++) {

                Element show = (Element) series.item(i);
                String showName = show.getElementsByTagName("SeriesName").item(0).getTextContent();
                if (savedShow.getTitle().toLowerCase().equals(showName.toLowerCase())) {

                    Integer showId = Integer.valueOf(show.getElementsByTagName("seriesid").item(0).getTextContent());
                    savedShow.setTvdbId(showId);

                    try {
                        savedShow.save();
                        Logger.info("Saving TvdbId for show: " + savedShow.getTitle());
                        nameIdMapping.setProperty(savedShow.getTitle(), showId.toString());
                    } catch (Exception e) {
                        Logger.error("Could not insert TvdbId for show: " + savedShow.getTitle());
                    } finally {
                        //since we already found our show, don't search any further
                        break;
                    }


                }

            }

        }

        nameIdMapping.store(new FileOutputStream("tvdbids.properties"), null);

    }

    List<Show> loadShows() throws ParserConfigurationException, SAXException, IOException {

        saveTvdbIds(true);
        List<Show> shows = new ArrayList<>();

        for (Show show : Show.find.where().isNotNull("tvdbId").findSet()) {

            Path xmlFile = Paths.get(this.dataPath, "tvdb", show.getTvdbId() + ".xml");
            if (!Files.exists(xmlFile)) {

                downloadXmlFile(xmlFile, show.getTvdbId());

            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document;
            try {
                document = documentBuilder.parse(String.valueOf(xmlFile));
            } catch (Exception e) {
                Logger.error("Could not parse details xml for show: " + show.getTitle());
                continue;
            }

            Map<Integer, Map<Integer, Episode>> seasons = new HashMap<>();

            //save show description
            Element series = (Element) document.getElementsByTagName("Series").item(0);
            String showDescription = series.getElementsByTagName("Overview").item(0).getTextContent();
            Day airday = Day.parseDay(series.getElementsByTagName("Airs_DayOfWeek").item(0).getTextContent());
            String airtime = series.getElementsByTagName("Airs_Time").item(0).getTextContent();
            show.setDescription(showDescription.replace("\n", ""));
            show.setAirday(airday);
            show.setAirtime(airtime);

            String networkName = series.getElementsByTagName("Network").item(0).getTextContent();
            if (networkName.length() > 0) {
                try {
                    Network existingNetwork = Network.find.where().eq("name", networkName).findUnique();
                    if (existingNetwork == null) {
                        Network network = new Network();
                        network.setName(networkName);
                        network.save();
                        show.setNetwork(network);
                    } else {
                        show.setNetwork(existingNetwork);
                    }
                } catch (Exception e) {
                }

            }

            String[] actors = series.getElementsByTagName("Actors").item(0).getTextContent().split("\\|");
            for (String actorName : actors) {
                actorName = actorName.trim();
                if (actorName.isEmpty()) {
                    continue;
                }
                Actor existingActor;
                try {
                    existingActor = Actor.find.where().eq("name", actorName).findUnique();
                } catch (Exception e) {
                    continue;
                }

                if (existingActor == null) {
                    Actor actor = new Actor();
                    actor.setName(actorName);
                    actor.save();
                    show.getActors().add(actor);

                } else {
                    show.getActors().add(existingActor);
                }

            }

            //save episode details
            NodeList episodes = document.getElementsByTagName("Episode");
            for (int i = 0; i < episodes.getLength(); i++) {

                Element episodeElement = (Element) episodes.item(i);

                int seasonNumber = Integer.parseInt(episodeElement.getElementsByTagName("SeasonNumber").item(0).getTextContent());
                if (seasonNumber == 0) {
                    //seasons < 1 are extra content, ignore it
                    continue;
                }

                int episodeNumber = Integer.parseInt(episodeElement.getElementsByTagName("EpisodeNumber").item(0).getTextContent());
                String episodeName = episodeElement.getElementsByTagName("EpisodeName").item(0).getTextContent();
                String airTime = episodeElement.getElementsByTagName("FirstAired").item(0).getTextContent();


                //@TODO fetch date from imdb data if missing in tvdb data
                Date airTimeParsed;
                try {
                    airTimeParsed = new SimpleDateFormat("yyyy-MM-dd").parse(airTime);
                } catch (Exception e) {
                    airTimeParsed = null;
                }
                String description = episodeElement.getElementsByTagName("Overview").item(0).getTextContent();

                Episode episode = createEpisode(episodeName, airTimeParsed, episodeNumber, description.replace("\n", ""));

                if (!seasons.containsKey(seasonNumber)) {
                    seasons.put(seasonNumber, new HashMap<Integer, Episode>());
                }

                seasons.get(seasonNumber).put(episodeNumber, episode);

            }

            if (show.getSeasons() != null) {
                for (Season season : show.getSeasons()) {
                    season.delete();
                }
            }

            addSeasonsToShow(show, seasons);
            shows.add(show);


        }

        return shows;

    }


}
