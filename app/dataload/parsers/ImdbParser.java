package dataload.parsers;

import dataload.provider.ImdbProvider;
import models.Episode;
import models.Season;
import models.Show;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImdbParser extends AbstractShowParser {

    private final static Pattern EPISODE_PATTERN = Pattern.compile(
            "\"(?<showName>.*)\"\\s\\(\\d+\\)\\s\\{(?<title>.+)\\(#(?<season>\\d+)" +
                    "\\.(?<episode>\\d+)\\)\\}\\s*USA:(?<day>\\d+)\\s(?<month>\\w+)\\s(?<year>\\d+)"
    );
    private final ImdbProvider imdbProvider;


    public ImdbParser(ImdbProvider imdbProvider) {

        this.imdbProvider = imdbProvider;

    }

    @Override
    public List<Show> parseShows() throws IOException, ParseException {

        List<Show> shows = new ArrayList<>();

        BufferedReader bufferedReader = imdbProvider.getFileReader();
        Show currentShow = null;
        String line;
        Map<Integer, Map<Integer, Episode>> seasons = new HashMap<>();

        while ((line = bufferedReader.readLine()) != null) {

            Matcher matcher = EPISODE_PATTERN.matcher(line);
            if (matcher.matches()) {

                Calendar calendar = createAirtime(matcher);

                String showName = matcher.group("showName").trim();
                int seasonNumber = Integer.parseInt(matcher.group("season"));
                int episodeNumber = Integer.parseInt(matcher.group("episode"));

                Episode episode = createEpisode(matcher.group("title").trim(), calendar.getTime(), episodeNumber, null);

                //initialize the first show
                if (currentShow == null) {
                    currentShow = createNewShow(showName);
                }

                //everything for a show was parsed, we move on to a new show
                if (!showName.equals(currentShow.getTitle())) {

                    addSeasonsToShow(currentShow, seasons);
                    shows.add(currentShow);
                    currentShow = createNewShow(showName);
                    seasons.clear();

                }

                if (!seasons.containsKey(seasonNumber)) {
                    seasons.put(seasonNumber, new HashMap<Integer, Episode>());
                }

                seasons.get(seasonNumber).put(episodeNumber, episode);

            }

        }

        // save the last show
        addSeasonsToShow(currentShow, seasons);
        shows.add(currentShow);

        return shows;

    }

    private Calendar createAirtime(Matcher matcher) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(matcher.group("year"));
        int month = getMonth(matcher.group("month"));
        int day = Integer.parseInt(matcher.group("day"));
        calendar.set(year, month, day, 0, 0, 0);

        return calendar;

    }

    private int getMonth(String month) throws ParseException {

        Date date = new SimpleDateFormat("MMMM", Locale.US).parse(month);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MONTH);

    }

    private Show createNewShow(String title) {

        Show show = new Show();
        show.setTitle(title);
        show.setSeasons(new ArrayList<Season>());
        return show;

    }

}
