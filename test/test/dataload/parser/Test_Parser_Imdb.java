package test.dataload.parser;

import dataload.parsers.ImdbParser;
import dataload.provider.ImdbProvider;
import models.Episode;
import models.Show;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class Test_Parser_Imdb {

    @Test
    public void parseShows() {

        ImdbProvider imdbProvider = new ImdbProvider(Paths.get("test/test/data", "release-dates.list"));
        ImdbParser imdbParser = new ImdbParser(imdbProvider);

        List<Show> shows = null;
        try {
            shows = imdbParser.parseShows();
        } catch (Exception e) {
            assertThat(true).overridingErrorMessage("Failure while parsing imdb test file").isFalse();
        }

        assertThat(shows).hasSize(3);

        Show firstShow = shows.get(0);
        List<Episode> firstShowEpisodes = firstShow.getSeasons().get(0).getEpisodes();
        assertThat(firstShow.getTitle()).isEqualTo("#1 Single");
        assertThat(firstShow.getSeasons()).hasSize(1);
        assertThat(firstShowEpisodes).hasSize(8);
        assertThat(firstShowEpisodes.get(0).getTitle()).isEqualTo("Is the Grass Greener?");
        assertThat(firstShowEpisodes.get(1).getTitle()).isEqualTo("Window Shopping");
        assertThat(firstShowEpisodes.get(2).getTitle()).isEqualTo("The Rules of Dating");
        assertThat(firstShowEpisodes.get(3).getTitle()).isEqualTo("Cats and Dogs");
        assertThat(firstShowEpisodes.get(4).getTitle()).isEqualTo("Finishing a Chapter");
        assertThat(firstShowEpisodes.get(5).getTitle()).isEqualTo("Wingman");
        assertThat(firstShowEpisodes.get(6).getTitle()).isEqualTo("Timing Is Everything");
        assertThat(firstShowEpisodes.get(7).getTitle()).isEqualTo("Stay");

        assertThat(firstShowEpisodes.get(0).getAirtime().toString()).startsWith("Sun Jan 22");

        Show secondShow = shows.get(1);
        assertThat(secondShow.getTitle()).isEqualTo("#Follow");
        assertThat(secondShow.getSeasons()).hasSize(1);
        assertThat(secondShow.getSeasons().get(0).getEpisodes()).hasSize(1);

        Show thirdShow = shows.get(2);
        assertThat(thirdShow.getTitle()).isEqualTo("#PrettyPeopleProblems");
        assertThat(thirdShow.getSeasons()).hasSize(2);
        assertThat(thirdShow.getSeasons().get(0).getEpisodes()).hasSize(5);
        assertThat(thirdShow.getSeasons().get(1).getEpisodes()).hasSize(1);


    }

}
