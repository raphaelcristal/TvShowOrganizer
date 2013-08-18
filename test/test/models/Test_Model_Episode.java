package test.models;

import models.Episode;
import models.Season;
import models.Show;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_Model_Episode {

    private Show createShow() {

        Show show = new Show();
        show.setTitle("Die Simpsons Test");
        show.setDescription("Die animierte Serie „The Simpsons“ erzählt die Abenteuer der gleichnamige gelben Familie, die in der fiktiven Stadt Springfield lebt");

        Season season = new Season();

        List<Season> seasons = new ArrayList<Season>();
        seasons.add(season);

        show.setSeasons(seasons);

        Episode episode01 = new Episode();
        episode01.setNumber(1);
        episode01.setTitle("Kuchen, Kopfgeld und Kautionen");
        episode01.setDescription("Der Inhalt dieser Episode darf nicht angezeigt werden. Fragen Sie ProSieben warum dies so ist....");

        Episode episode02 = new Episode();
        episode02.setNumber(2);
        episode02.setTitle("Jäger des verlorenen Handys");
        episode02.setDescription("Der Inhalt dieser Episode darf nicht angezeigt werden. Fragen Sie ProSieben warum dies so ist....");

        List<Episode> episodes = new ArrayList<Episode>();
        episodes.add(episode01);
        episodes.add(episode02);

        season.setEpisodes(episodes);

        show.save();
        return show;

    }


    @Test
    public void selectEpisodeById() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                List<Episode> episodes = createShow().getSeasons().get(0).getEpisodes();
                Episode episode = episodes.get(0);


                Episode savedEpisode = Episode.find.byId(episode.getId());
                assertThat(savedEpisode.getNumber()).isEqualTo(episode.getNumber());
                assertThat(savedEpisode.getTitle()).isEqualTo(episode.getTitle());

            }
        });
    }

    @Test
    public void updateEpisode() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                String newDescription = "Erste Folge der neuen Staffel";

                Season season = createShow().getSeasons().get(0);
                Episode savedEpisode = season.getEpisodes().get(0);
                savedEpisode.setDescription(newDescription);

                season.save();

                Episode updateEpisode = Episode.find.byId(savedEpisode.getId());
                assertThat(updateEpisode.getDescription()).isEqualTo(newDescription);

            }
        });
    }

    @Test
    public void deleteEpisodes() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                List<Episode> episodes = createShow().getSeasons().get(0).getEpisodes();
                Episode episode = episodes.get(0);
                Long episodeId = episode.getId();

                episode.delete();

                assertThat(Episode.find.byId(episode.getId())).isNull();

            }
        });
    }

}