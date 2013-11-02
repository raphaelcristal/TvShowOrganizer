package dataload.provider;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.Logger;
import play.libs.WS;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import static play.libs.F.Promise;

public class TvdbProvider {

    private final static String SEARCH_URL = "http://thetvdb.com/api/GetSeries.php";
    private final String updateUrlDay;
    private final String updateUrlWeek;
    private final String updateUrlMonth;
    private final String showUrl;
    private final String episodeUrl;
    private Properties titleIdMapping = null;
    private String titleIdMappingPath = null;
    private Frequency frequency = null;

    public enum Frequency {
        DAILY,
        WEEKLY,
        MONTHLY
    }


    /**
     * Register for a token here: http://thetvdb.com/?tab=apiregister
     *
     * @param token your tvdb token
     */
    public TvdbProvider(String token) {
        this.updateUrlDay = String.format("http://thetvdb.com/api/%s/updates/updates_day.xml", token);
        this.updateUrlWeek = String.format("http://thetvdb.com/api/%s/updates/updates_week.xml", token);
        this.updateUrlMonth = String.format("http://thetvdb.com/api/%s/updates/updates_month.xml", token);
        this.showUrl = "http://thetvdb.com/api/" + token + "/series/%s/all/en.xml";
        this.episodeUrl = "http://thetvdb.com/api/" + token + "/episodes/%s/en.xml";
    }

    public TvdbProvider(String token, String titleIdMappingPath, Frequency frequency) throws IOException {
        this(token);
        this.titleIdMappingPath = titleIdMappingPath;
        this.frequency = frequency;
        this.titleIdMapping = new Properties();
    }


    public Promise<WS.Response> fetchShowAsync(int tvdbId) {
        return WS.url(String.format(showUrl, tvdbId)).get();
    }

    public Promise<WS.Response> searchShowAsync(String title) {
        return WS.url(SEARCH_URL).setQueryParameter("seriesname", title).get();
    }

    /**
     * Fetch a tvdb id from the properties file
     *
     * @param title title of the show
     * @return the id or -1 if id was not found
     */
    public int getIdByNameFromFile(String title) {
        return Integer.parseInt(titleIdMapping.getProperty(title, "-1"));
    }

    /**
     * Save the mapping file to file
     *
     * @throws IOException
     */
    public void saveTitleIdMappingToFile() throws IOException {
        titleIdMapping.store(new FileOutputStream(titleIdMappingPath), null);
    }

    /**
     * Fetch a tvdb id
     *
     * @param title title of the show
     * @return the id or -1 if id was not found
     */
    public int getIdByNameFromApi(String title) {

        Document root = null;
        try {
            root = searchShows(title);
        } catch (Exception e) {
            Logger.error("Error while searching for shows on tvdb with name: " + title, e);
            return -1;
        }

        NodeList series = root.getElementsByTagName("Series");
        for (int i = 0; i < series.getLength(); i++) {

            Element show = (Element) series.item(i);
            String showName = show.getElementsByTagName("SeriesName").item(0).getTextContent();
            if (title.toLowerCase().equals(showName.toLowerCase())) {

                Integer id = Integer.valueOf(show.getElementsByTagName("seriesid").item(0).getTextContent());
                this.titleIdMapping.setProperty(title, String.valueOf(id));
                return id;

            }

        }

        return -1;

    }

    private Document searchShows(String name) throws IOException, ParserConfigurationException, SAXException {
        return fetchXml(String.format(SEARCH_URL + "?seriesname=%s", URLEncoder.encode(name, "UTF-8")));
    }

    public Document fetchShow(Integer id) throws IOException, ParserConfigurationException, SAXException {
        return fetchXml(String.format(showUrl, id));
    }

    public Document fetchEpisode(Integer id) throws Exception {
        return fetchXml(String.format(episodeUrl, id));
    }

    public Document fetchUpdates() throws Exception {

        switch (frequency) {
            case DAILY:
                return fetchXml(updateUrlDay);
            case WEEKLY:
                return fetchXml(updateUrlWeek);
            case MONTHLY:
                return fetchXml(updateUrlMonth);
            default:
                return fetchXml(updateUrlDay);
        }

    }

    private Document fetchXml(String url) throws IOException, ParserConfigurationException, SAXException {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response.getEntity().writeTo(output);

        return parseXml(output);

    }

    private Document parseXml(ByteArrayOutputStream outputStream) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(outputStream.toByteArray()));
    }


}
