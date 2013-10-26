package dataload.provider;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class TvdbProvider {

    private final static String SEARCH_URL = "http://thetvdb.com/api/GetSeries.php?seriesname=%s";
    private final String updateUrlDay;
    private final String updateUrlWeek;
    private final String updateUrlMonth;
    private final String showUrl;
    private final String episodeUrl;

    public enum Updates {
        DAY,
        WEEK,
        MONTH
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

    public Document searchShows(String name) throws IOException, ParserConfigurationException, SAXException {
        return fetchXml(String.format(SEARCH_URL, URLEncoder.encode(name, "UTF-8")));
    }

    public Document fetchShow(Integer id) throws IOException, ParserConfigurationException, SAXException {
        return fetchXml(String.format(showUrl, id));
    }

    public Document fetchEpisode(String id) throws Exception {
        return fetchXml(String.format(episodeUrl, id));
    }

    public Document fetchUpdates(Updates updates) throws Exception {

        switch (updates) {
            case DAY:
                return fetchXml(updateUrlDay);
            case WEEK:
                return fetchXml(updateUrlWeek);
            case MONTH:
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
