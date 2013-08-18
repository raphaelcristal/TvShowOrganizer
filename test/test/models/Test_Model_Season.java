package test.models;

import models.Season;
import models.Show;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_Model_Season {

    private Show createShow() {

        Show show = new Show();
        show.setTitle("Darkwing Duck");
        show.setDescription("Description Of Darkwing Duck");

        List<Season> seasons = new ArrayList<Season>();
        Season season = new Season();
        seasons.add(season);

        show.setSeasons(seasons);

        show.save();

        return show;

    }

    @Test
    public void selectSeasonByShow() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show savedShow = createShow();
                Show show = Show.find.byId(savedShow.getId());

                List<Season> seasons = show.getSeasons();
                Season season = seasons.get(0);

                assertThat(season).isNotNull();

            }
        });
    }

    @Test
    public void selectSeasonById() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Season savedSeason = createShow().getSeasons().get(0);
                Season season = Season.find.byId(savedSeason.getId());

                assertThat(season).isNotNull();

            }
        });
    }

    @Test
    public void deleteSeason() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show savedShow = createShow();
                Show show = Show.find.byId(savedShow.getId());

                List<Season> seasons = show.getSeasons();
                Long seasonId = seasons.get(0).getId();
                Season season = seasons.get(0);
                season.delete();

                assertThat(Season.find.byId(seasonId)).isNull();

            }
        });
    }

}