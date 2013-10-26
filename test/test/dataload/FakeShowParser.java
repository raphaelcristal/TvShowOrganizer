package test.dataload;

import dataload.parsers.AbstractShowParser;
import models.Show;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FakeShowParser extends AbstractShowParser {

    private List<Show> shows = new ArrayList<>();

    public void addShow(Show show) {
        shows.add(show);
    }

    @Override
    public List<Show> parseShows() throws IOException, ParseException {
        return shows;
    }
}
