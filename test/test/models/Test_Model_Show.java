package test.models;

import models.Day;
import models.Show;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_Model_Show {

    private Show createShow() {

        Show show = new Show();
        show.setTitle("Duck Tales Test 02");
        show.setDescription("Description Of Duck Tales");
        show.setAirday(Day.SATURDAY);
        show.save();

        return show;

    }

    @Test
    public void selectShowById() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show savedShow = createShow();

                Show show = Show.find.byId(savedShow.getId());
                assertThat(show.getId()).isEqualTo(savedShow.getId());
                assertThat(show.getTitle()).isEqualTo(savedShow.getTitle());
                assertThat(show.getDescription()).isEqualTo(savedShow.getDescription());
                assertThat(show.getAirday()).isEqualTo(savedShow.getAirday());

            }
        });
    }

    @Test
    public void selectShowByTitle() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show savedShow = createShow();
                String title = savedShow.getTitle();
                Show show = Show.find.where().eq("title", savedShow.getTitle()).findUnique();

                assertThat(show.getId()).isEqualTo(savedShow.getId());
                assertThat(show.getTitle()).isEqualTo(savedShow.getTitle());
                assertThat(show.getDescription()).isEqualTo(savedShow.getDescription());

            }
        });
    }

    @Test
    public void updateShow() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                String newTitle = "Duck Tales - Neues aus Entenhausen";
                String newDescription = "Duck Tales - Neues aus Entenhausen";

                Show savedShow = createShow();
                Long id = savedShow.getId();
                savedShow.setTitle(newTitle);
                savedShow.setDescription(newDescription);
                savedShow.save();

                Show updatedShow = Show.find.byId(id);

                assertThat(savedShow.getTitle()).isEqualTo(newTitle);
                assertThat(savedShow.getDescription()).isEqualTo(newDescription);

            }
        });
    }

    @Test
    public void deleteShow() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Show show = createShow();
                Long id = show.getId();
                show.delete();

                Show deletedShow = Show.find.byId(id);

                assertThat(deletedShow).isNull();

            }
        });
    }

}