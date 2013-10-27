package test.dataload.parser;

import dataload.parsers.TvdbParser;
import dataload.provider.TvdbProvider;
import models.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.*;

public class Test_Parser_Tvdb {

    private final static Integer TVDB_ID_SHOW = 75978;
    private final static Integer TVDB_ID_EPISODE = 1230461;

    private void createFakeShow() {

        Show show = new Show();
        show.setTvdbId(TVDB_ID_SHOW);
        show.setTitle("Family Guy");

        show.save();

    }

    private Document parseXmlFile(String path) throws ParserConfigurationException, IOException, SAXException {

        File xmlFile = new File(path);

        return DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(xmlFile);
    }

    @Test
    public void parseShow() throws ParserConfigurationException, SAXException, IOException, ParseException {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                createFakeShow();
                List<Show> shows = null;
                try {
                    TvdbProvider tvdbProvider = mock(TvdbProvider.class);
                    when(tvdbProvider.getIdByNameFromFile("Family Guy")).thenReturn(TVDB_ID_SHOW);
                    when(tvdbProvider.fetchShow(TVDB_ID_SHOW)).thenReturn(parseXmlFile(String.format("test/test/data/%s.xml", TVDB_ID_SHOW)));

                    TvdbParser tvdbParser = new TvdbParser(tvdbProvider, true);
                    shows = tvdbParser.parseShows();
                } catch (Exception e) {
                    assertThat(true).overridingErrorMessage("Something went wrong while parsing the test tvdb data").isFalse();
                }

                assertThat(shows).hasSize(1);

                Show show = shows.get(0);
                assertThat(show.getAirday()).isEqualTo(Day.SUNDAY);
                assertThat(show.getAirtime()).isEqualTo("9:00 PM");
                assertThat(show.getDescription()).startsWith("Sick, twisted, politically incorrect and Freakin' Sweet");
                assertThat(show.getNetwork().getName()).isEqualTo("FOX");
                assertThat(show.getTitle()).isEqualTo("Family Guy");
                assertThat(show.getTvdbId()).isEqualTo(TVDB_ID_SHOW);

                Set<Actor> actors = show.getActors();
                assertThat(actors).hasSize(7);

                List<Season> seasons = show.getSeasons();
                assertThat(seasons).hasSize(12);

                Season firstSeason = seasons.get(0);
                assertThat(firstSeason.getEpisodes()).hasSize(7);

                Episode firstEpisode = firstSeason.getEpisodes().get(0);
                assertThat(firstEpisode.getAirtime().toString()).isEqualTo("Sun Jan 31 00:00:00 CET 1999");
                assertThat(firstEpisode.getDescription()).startsWith("After Peter heavily drinks at a bachelor party");
                assertThat(firstEpisode.getNumber()).isEqualTo(1);
                assertThat(firstEpisode.getTitle()).isEqualTo("Death has a Shadow");
            }
        });


    }

    @Test
    public void parseUpdatesForShow() throws ParserConfigurationException, SAXException, IOException, ParseException {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                createFakeShow();
                List<Show> shows = null;
                try {
                    TvdbProvider tvdbProvider = mock(TvdbProvider.class);
                    when(tvdbProvider.fetchShow(TVDB_ID_SHOW)).thenReturn(parseXmlFile(String.format("test/test/data/%s.xml", TVDB_ID_SHOW)));
                    when(tvdbProvider.fetchUpdates()).thenReturn(parseXmlFile("test/test/data/updates_day_show.xml"));

                    TvdbParser tvdbParser = new TvdbParser(tvdbProvider, true);
                    shows = tvdbParser.parseUpdates();
                } catch (Exception e) {
                    assertThat(true).overridingErrorMessage("Something went wrong while parsing the test tvdb data").isFalse();
                }

                assertThat(shows).hasSize(1);

                Show show = shows.get(0);
                assertThat(show.getAirday()).isEqualTo(Day.SUNDAY);
                assertThat(show.getAirtime()).isEqualTo("9:00 PM");
                assertThat(show.getDescription()).startsWith("Sick, twisted, politically incorrect and Freakin' Sweet");
                assertThat(show.getNetwork().getName()).isEqualTo("FOX");
                assertThat(show.getTitle()).isEqualTo("Family Guy");
                assertThat(show.getTvdbId()).isEqualTo(TVDB_ID_SHOW);

                Set<Actor> actors = show.getActors();
                assertThat(actors).hasSize(7);

                List<Season> seasons = show.getSeasons();
                assertThat(seasons).hasSize(12);

                Season firstSeason = seasons.get(0);
                assertThat(firstSeason.getEpisodes()).hasSize(7);

                Episode firstEpisode = firstSeason.getEpisodes().get(0);
                assertThat(firstEpisode.getAirtime().toString()).isEqualTo("Sun Jan 31 00:00:00 CET 1999");
                assertThat(firstEpisode.getDescription()).startsWith("After Peter heavily drinks at a bachelor party");
                assertThat(firstEpisode.getNumber()).isEqualTo(1);
                assertThat(firstEpisode.getTitle()).isEqualTo("Death has a Shadow");
            }
        });


    }

    @Test
    public void parseUpdatesForEpisode() throws ParserConfigurationException, SAXException, IOException, ParseException {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                createFakeShow();
                List<Show> shows = null;
                try {
                    TvdbProvider tvdbProvider = mock(TvdbProvider.class);
                    when(tvdbProvider.fetchUpdates()).thenReturn(parseXmlFile("test/test/data/updates_day_episode.xml"));
                    when(tvdbProvider.fetchEpisode(TVDB_ID_EPISODE)).thenReturn(parseXmlFile(String.format("test/test/data/%s.xml", TVDB_ID_EPISODE)));

                    TvdbParser tvdbParser = new TvdbParser(tvdbProvider, true);
                    shows = tvdbParser.parseUpdates();
                } catch (Exception e) {
                    assertThat(true).overridingErrorMessage("Something went wrong while parsing the test tvdb data").isFalse();
                }

                assertThat(shows).hasSize(1);

                Show show = shows.get(0);
                assertThat(show.getTitle()).isEqualTo("Family Guy");

                List<Season> seasons = show.getSeasons();
                assertThat(seasons).hasSize(1);

                Season eigthSeason = seasons.get(0);
                assertThat(eigthSeason.getNumber()).isEqualTo(8);
                assertThat(eigthSeason.getEpisodes()).hasSize(1);

                Episode firstEpisode = eigthSeason.getEpisodes().get(0);
                assertThat(firstEpisode.getAirtime().toString()).isEqualTo("Sun Jan 31 00:00:00 CET 2010");
                assertThat(firstEpisode.getDescription()).startsWith("Brian gets a gig writing for a teen magazine");
                assertThat(firstEpisode.getNumber()).isEqualTo(11);
                assertThat(firstEpisode.getTitle()).isEqualTo("Dial Meg for Murder");
            }
        });


    }


}
