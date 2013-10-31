package test.routes;

import controllers.routes;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import test.data.TestData;

import java.util.Calendar;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class Test_Routes_Show {

    @Test
    public void getShowById() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/1"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("title").asText()).isEqualTo("Die Simpsons");
                assertThat(json.get("description").asText()).startsWith("Die Simpsons ist eine von Matt Groening geschaffene, vielfach ausgezeichnete US-amerikanische Zeichentrickserie");

                assertThat(json.get("seasons").size()).isEqualTo(2);

                JsonNode firstSeason = json.get("seasons").get(0);
                JsonNode thirdEpisode = firstSeason.get("episodes").get(2);
                long timestamp = thirdEpisode.get("airtime").asLong();
                Date airtime = new Date(timestamp);

                Calendar calender = Calendar.getInstance();
                calender.set(1990, Calendar.JANUARY, 21, 0, 0, 0);
                Date expectedDate = calender.getTime();

                assertThat(thirdEpisode.get("number").asInt()).isEqualTo(3);
                assertThat(thirdEpisode.get("title").asText()).isEqualTo("Der Versager");
                assertThat(thirdEpisode.get("description").asText()).isEqualTo("Keine Beschreibung verfügbar!");
                assertThat(airtime.toString()).isEqualTo(expectedDate.toString());


            }
        });
    }

    @Test
    public void getMissingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                Result result = routeAndCall(fakeRequest(GET, "/shows/999"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Show does not exist.");

            }
        });
    }

    @Test
    public void getMissingShowByName() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                Result result = routeAndCall(fakeRequest(GET, "/shows/search?title=qwe"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.toString()).isEqualTo("[]");


            }
        });
    }

    @Test
    public void getShowByName() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/search?title=Simpsons"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode show = json.get(0);

                assertThat(show.get("id").asLong()).isEqualTo(1L);
                assertThat(show.get("title").asText()).isEqualTo("Die Simpsons");


            }
        });


    }

    @Test
    public void addAnExistingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = callAction(routes.ref.Shows.addShow(1));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                assertThat(json.get("error").getTextValue()).isEqualTo("Show already exists.");


            }
        });


    }

}
