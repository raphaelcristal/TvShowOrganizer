package test.dataload;

import com.google.common.collect.ImmutableList;
import dataload.AbstractDataLoad;
import models.*;
import org.junit.Test;
import play.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_DataLoadUpdate {

    private Show createShow(String title) {
        Show show = new Show();
        show.setTitle(title);
        return show;
    }

    private void loadShows(AbstractDataLoad dataLoad) {
        try {
            dataLoad.saveShows();
        } catch (Exception e) {
            assertThat(true).overridingErrorMessage("MockDataLoad: Could not import show. " + e).isFalse();
        }
    }

    @Test
    public void updateShowWithNoNetworkWithNewNetwork() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow("test");
                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                Network network = new Network();
                network.setName("test");
                show.setNetwork(network);

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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
                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                Network network = new Network();
                network.setName("test");
                network.save();
                show.setNetwork(network);

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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
                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                Network newNetwork = new Network();
                newNetwork.setName("test2");
                show.setNetwork(newNetwork);
                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                Network newNetwork = new Network();
                newNetwork.setName("test2");
                newNetwork.save();
                show.setNetwork(newNetwork);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                Actor newActor = new Actor();
                newActor.setName("peter");
                Show importedShow = Show.find.where().eq("title", "test").findUnique();
                importedShow.getActors().add(newActor);

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(importedShow);
                loadShows(mockDataLoad);

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
                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                Season season = new Season();
                season.setNumber(1);
                show.setSeasons(ImmutableList.of(season));

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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
                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                Season newSeason = new Season();
                newSeason.setNumber(2);
                show.setSeasons(ImmutableList.of(newSeason));

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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
                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                season = new Season();
                season.setNumber(1);
                Episode episode = new Episode();
                episode.setNumber(1);
                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                season = new Season();
                season.setNumber(1);

                episode.setNumber(1);
                episode.setAirtime(new Date(2000));
                episode.setDescription("description2");
                episode.setTitle("title2");

                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                show = createShow("test");
                season = new Season();
                season.setNumber(1);

                episode.setNumber(1);

                season.setEpisodes(ImmutableList.of(episode));
                show.setSeasons(ImmutableList.of(season));

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(createShow("a test show"));
                loadShows(mockDataLoad);

                Network network = new Network();
                network.setName("a test network");

                Show show = createShow("a test show");
                show.setAirday(Day.MONDAY);
                show.setAirtime("8:00 PM");
                show.setDescription("a test description");
                show.setTvdbId(5);

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

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

                MockDataLoad mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(createShow("a test show"));
                loadShows(mockDataLoad);

                Network network = new Network();
                network.setName("a test network");

                Show show = createShow("a test show");
                show.setAirday(Day.MONDAY);
                show.setAirtime("8:00 PM");
                show.setDescription("a test description");
                show.setTvdbId(5);

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(show);
                loadShows(mockDataLoad);

                Show updateShow = createShow("a test show");
                updateShow.setAirday(null);
                updateShow.setAirtime(null);
                updateShow.setDescription(null);
                updateShow.setTvdbId(null);

                mockDataLoad = new MockDataLoad();
                mockDataLoad.addShow(updateShow);
                loadShows(mockDataLoad);

                Show importedShow = Show.find.where().eq("title", "a test show").findUnique();

                assertThat(importedShow.getAirday()).isNotNull();
                assertThat(importedShow.getAirtime()).isNotNull();
                assertThat(importedShow.getDescription()).isNotNull();
                assertThat(importedShow.getTvdbId()).isNotNull();

            }
        });
    }
}
