package test.dataload;

import dataload.AbstractDataLoad;
import models.Show;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MockDataLoad extends AbstractDataLoad {

    private List<Show> shows = new ArrayList<>();

    public void addShow(Show show) {
        shows.add(show);
    }

    @Override
    public List<Show> loadShows() throws IOException, ParseException, ParserConfigurationException, SAXException {
        return shows;
    }
}
