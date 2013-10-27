package test.dataload;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dataload.DataLoad;
import dataload.parsers.AbstractShowParser;
import models.*;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_DataLoadImport {

    private Show createShow(String title) {
        Show show = new Show();
        show.setTitle(title);
        return show;
    }

    private void loadShows(AbstractShowParser fakeShowParser) {
        try {
            DataLoad dataLoad = new DataLoad(ImmutableList.of(fakeShowParser));
            dataLoad.importShows();
        } catch (Exception e) {
            assertThat(true).overridingErrorMessage("FakeShowParser: Could not import show. " + e).isFalse();
        }
    }

    private void loadUpdates(AbstractShowParser fakeShowParser) {
        try {
            DataLoad dataLoad = new DataLoad(ImmutableList.of(fakeShowParser));
            dataLoad.updateShows();
        } catch (Exception e) {
            assertThat(true).overridingErrorMessage("FakeShowParser: Could not update show. " + e).isFalse();
        }
    }

    @Test
    public void importNewShow() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("title");

                show.setAirday(Day.MONDAY);
                show.setAirtime("1:00 PM");
                show.setDescription("description");
                show.setTvdbId(1);

                Season season1 = new Season();
                season1.setNumber(1);
                Season season2 = new Season();
                season2.setNumber(2);

                Episode episode1 = new Episode();
                episode1.setNumber(1);
                episode1.setAirtime(new Date(1000));
                episode1.setDescription("description");
                episode1.setTitle("title");
                Episode episode2 = new Episode();
                episode2.setNumber(1);
                episode2.setAirtime(new Date(1000));
                episode2.setDescription("description");
                episode2.setTitle("title");

                season1.setEpisodes(ImmutableList.of(episode1));
                season2.setEpisodes(ImmutableList.of(episode2));

                show.setSeasons(ImmutableList.of(season1, season2));

                Network network = new Network();
                network.setName("network");
                show.setNetwork(network);

                Actor actor1 = new Actor();
                actor1.setName("actor1");
                Actor actor2 = new Actor();
                actor2.setName("actor2");
                show.setActors(ImmutableSet.of(actor1, actor2));

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "title").findUnique();

                assertThat(importedShow.getAirday()).isEqualTo(Day.MONDAY);
                assertThat(importedShow.getAirtime()).isEqualTo("1:00 PM");
                assertThat(importedShow.getDescription()).isEqualTo("description");
                assertThat(importedShow.getTitle()).isEqualTo("title");
                assertThat(importedShow.getTvdbId()).isEqualTo(1);
                assertThat(importedShow.getNetwork().getName()).isEqualTo("network");
                assertThat(importedShow.getActors()).contains(actor1);
                assertThat(importedShow.getActors()).contains(actor2);
                assertThat(importedShow.getSeasons().get(0)).isEqualTo(season1);
                assertThat(importedShow.getSeasons().get(1)).isEqualTo(season2);

                assertThat(importedShow.getSeasons().get(0).getEpisodes().get(0).getNumber()).isEqualTo(1);
                assertThat(importedShow.getSeasons().get(0).getEpisodes().get(0).getAirtime()).isEqualTo(new Date(1000));
                assertThat(importedShow.getSeasons().get(0).getEpisodes().get(0).getDescription()).isEqualTo("description");
                assertThat(importedShow.getSeasons().get(0).getEpisodes().get(0).getTitle()).isEqualTo("title");

                assertThat(importedShow.getSeasons().get(1).getEpisodes().get(0).getNumber()).isEqualTo(1);
                assertThat(importedShow.getSeasons().get(1).getEpisodes().get(0).getAirtime()).isEqualTo(new Date(1000));
                assertThat(importedShow.getSeasons().get(1).getEpisodes().get(0).getDescription()).isEqualTo("description");
                assertThat(importedShow.getSeasons().get(1).getEpisodes().get(0).getTitle()).isEqualTo("title");

            }
        });
    }

    @Test
    public void updateShowWithNoNetworkWithNewNetwork() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                Network network = new Network();
                network.setName("test");
                show.setNetwork(network);

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();
                assertThat(importedShow.getNetwork().getName()).isEqualTo("test");

            }
        });
    }

    @Test
    public void updateShowWithNoNetworkWithExistingNetwork() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                Network network = new Network();
                network.setName("test");
                network.save();
                show.setNetwork(network);

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();
                assertThat(importedShow.getNetwork().getName()).isEqualTo("test");

            }
        });
    }

    @Test
    public void updateShowWithNetworkWithNewNetwork() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Network network = new Network();
                network.setName("test");
                show.setNetwork(network);
                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                Network newNetwork = new Network();
                newNetwork.setName("test2");
                show.setNetwork(newNetwork);
                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();
                assertThat(importedShow.getNetwork().getName()).isEqualTo("test2");

            }
        });
    }

    @Test
    public void updateShowWithNetworkWithExistingNetwork() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Network network = new Network();
                network.setName("test");
                network.save();
                show.setNetwork(network);

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Network newNetwork = new Network();
                newNetwork.setName("test2");
                newNetwork.save();
                show.setNetwork(newNetwork);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();
                assertThat(importedShow.getNetwork().getName()).isEqualTo("test2");

            }
        });
    }

    @Test
    public void updateShowWithNoActorsWithNewActor() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Actor actor = new Actor();
                actor.setName("hans");
                show.getActors().add(actor);

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();

                assertThat(importedShow.getActors()).contains(actor);

            }
        });
    }

    @Test
    public void updateShowWithNoActorsWithExistingActor() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Actor actor = new Actor();
                actor.setName("hans");
                actor.save();
                show.getActors().add(actor);

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();

                assertThat(importedShow.getActors()).contains(actor);

            }
        });
    }

    @Test
    public void updateShowWithOneActorWithNewActor() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Actor actor = new Actor();
                actor.setName("hans");
                show.getActors().add(actor);

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Actor newActor = new Actor();
                newActor.setName("peter");
                Show importedShow = Show.find.where().eq("title", "test").findUnique();
                importedShow.getActors().add(newActor);

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(importedShow);
                loadShows(fakeShowParser);

                importedShow = Show.find.where().eq("title", "test").findUnique();

                assertThat(importedShow.getActors()).contains(actor);
                assertThat(importedShow.getActors()).contains(newActor);

            }
        });
    }

    @Test
    public void updateShowWithNoSeasonWithNewSeason() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                Season season = new Season();
                season.setNumber(1);
                show.setSeasons(ImmutableList.of(season));

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();

                assertThat(importedShow.getSeasons()).hasSize(1);
                assertThat(importedShow.getSeasons()).contains(season);

            }
        });
    }

    @Test
    public void updateShowWithExistingSeasonWithNewSeason() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Season season = new Season();
                season.setNumber(1);
                show.setSeasons(ImmutableList.of(season));
                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                Season newSeason = new Season();
                newSeason.setNumber(2);
                show.setSeasons(ImmutableList.of(newSeason));

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();

                assertThat(importedShow.getSeasons()).hasSize(2);
                assertThat(importedShow.getSeasons()).contains(newSeason);
                assertThat(importedShow.getSeasons()).contains(newSeason);

            }
        });
    }

    @Test
    public void updateShowWithExistingSeasonWithNewEpisode() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Season season = new Season();
                season.setNumber(1);
                show.setSeasons(ImmutableList.of(season));
                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                season = new Season();
                season.setNumber(1);
                Episode episode = new Episode();
                episode.setNumber(1);
                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();

                assertThat(importedShow.getSeasons().get(0).getEpisodes()).hasSize(1);
                assertThat(importedShow.getSeasons().get(0).getEpisodes()).contains(episode);

            }
        });
    }

    @Test
    public void updateShowWithExistingSeasonWithExistingEpisode() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Season season = new Season();
                season.setNumber(1);

                Episode episode = new Episode();
                episode.setNumber(1);
                episode.setAirtime(new Date(1000));
                episode.setDescription("description");
                episode.setTitle("title");

                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                season = new Season();
                season.setNumber(1);

                episode.setNumber(1);
                episode.setAirtime(new Date(2000));
                episode.setDescription("description2");
                episode.setTitle("title2");

                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();

                List<Episode> episodes = importedShow.getSeasons().get(0).getEpisodes();
                Episode updatedEpisode = episodes.get(0);

                assertThat(episodes).hasSize(1);
                assertThat(updatedEpisode.getAirtime()).isEqualTo(new Date(2000));
                assertThat(updatedEpisode.getDescription()).isEqualTo("description2");
                assertThat(updatedEpisode.getTitle()).isEqualTo("title2");

            }
        });
    }

    @Test
    public void dontUpdateEpisodeWithNullValues() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                Season season = new Season();
                season.setNumber(1);

                Episode episode = new Episode();
                episode.setNumber(1);
                episode.setAirtime(new Date(1000));
                episode.setDescription("description");
                episode.setTitle("title");

                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                show = createShow("test");
                season = new Season();
                season.setNumber(1);

                episode.setNumber(1);

                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "test").findUnique();

                List<Episode> episodes = importedShow.getSeasons().get(0).getEpisodes();
                Episode updatedEpisode = episodes.get(0);

                assertThat(episodes).hasSize(1);
                assertThat(updatedEpisode.getAirtime()).isNotNull();
                assertThat(updatedEpisode.getDescription()).isNotNull();
                assertThat(updatedEpisode.getTitle()).isNotNull();

            }
        });
    }


    @Test
    public void updateExistingShow() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(createShow("a test show"));
                loadShows(fakeShowParser);

                Network network = new Network();
                network.setName("a test network");

                Show show = createShow("a test show");
                show.setAirday(Day.MONDAY);
                show.setAirtime("8:00 PM");
                show.setDescription("a test description");
                show.setTvdbId(5);

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "a test show").findUnique();

                assertThat(importedShow.getAirday()).isEqualTo(Day.MONDAY);
                assertThat(importedShow.getAirtime()).isEqualTo("8:00 PM");
                assertThat(importedShow.getDescription()).isEqualTo("a test description");
                assertThat(importedShow.getTvdbId()).isEqualTo(5);

            }
        });
    }

    @Test
    public void dontUpdateExistingShowWithNullValues() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(createShow("a test show"));
                loadShows(fakeShowParser);

                Network network = new Network();
                network.setName("a test network");

                Show show = createShow("a test show");
                show.setAirday(Day.MONDAY);
                show.setAirtime("8:00 PM");
                show.setDescription("a test description");
                show.setTvdbId(5);

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show updateShow = createShow("a test show");
                updateShow.setAirday(null);
                updateShow.setAirtime(null);
                updateShow.setDescription(null);
                updateShow.setTvdbId(null);

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(updateShow);
                loadShows(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "a test show").findUnique();

                assertThat(importedShow.getAirday()).isNotNull();
                assertThat(importedShow.getAirtime()).isNotNull();
                assertThat(importedShow.getDescription()).isNotNull();
                assertThat(importedShow.getTvdbId()).isNotNull();

            }
        });
    }

    @Test
    public void insertNewEpisodeDuringUpdate() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                FakeShowParser fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(createShow("a test show"));
                loadShows(fakeShowParser);

                Show show = createShow("a test show");
                show.setAirday(Day.MONDAY);
                show.setAirtime("8:00 PM");
                show.setDescription("a test description");
                show.setTvdbId(5);

                Season season = new Season();
                season.setNumber(1);

                Episode episode = new Episode();
                episode.setNumber(1);
                episode.setTitle("testtitle");

                season.setEpisodes(ImmutableList.of(episode));

                show.setSeasons(ImmutableList.of(season));

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addShow(show);
                loadShows(fakeShowParser);

                Show updateShow = createShow("a test show");

                Season seasonUpdate = new Season();
                season.setNumber(1);

                Episode episodeUpdate = new Episode();
                episode.setNumber(2);
                episode.setTitle("testtitle2");

                seasonUpdate.setEpisodes(ImmutableList.of(episode));
                updateShow.setSeasons(ImmutableList.of(season));

                fakeShowParser = new FakeShowParser();
                fakeShowParser.addUpdate(updateShow);
                loadUpdates(fakeShowParser);

                Show importedShow = Show.find.where().eq("title", "a test show").findUnique();

                assertThat(importedShow.getSeasons()).hasSize(1);
                Season firstSeason = importedShow.getSeasons().get(0);
                for (Episode updatedEpisode : firstSeason.getEpisodes()) {
                    if (updatedEpisode.getNumber() == 1) {
                        assertThat(updatedEpisode.getTitle()).isEqualTo("testtitle");
                    }
                    if (updatedEpisode.getNumber() == 2) {
                        assertThat(updatedEpisode.getTitle()).isEqualTo("testtitle2");
                    }

                }
            }
        });
    }
}
