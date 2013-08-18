package test.routes;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import test.data.TestData;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class Test_Routes_Season {

    @Test
    public void getSeasonById() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/seasons/1"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode firstEpisode = json.get("episodes").get(0);

                assertThat(firstEpisode.get("id").asLong()).isEqualTo(1L);
                assertThat(firstEpisode.get("title").asText()).isEqualTo("Es weihnachtet schwer");
                assertThat(firstEpisode.get("number").asInt()).isEqualTo(1);

            }
        });

    }

    @Test
    public void getSeasonForShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/1/seasons/2"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("id").asInt()).isEqualTo(2);
                JsonNode firstEpisode = json.get("episodes").get(0);
                assertThat(firstEpisode.get("id").asLong()).isEqualTo(5L);
                assertThat(firstEpisode.get("title").asText()).isEqualTo("Der Musterschüler");
            }
        });

    }

    @Test
    public void getAllSeasonsForShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/2/seasons"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                assertThat(json.size()).isEqualTo(1);

            }
        });

    }

    @Test
    public void getSeasonForMissingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/100/seasons/1"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Show does not exist.");


            }
        });

    }

    @Test
    public void getMissingSeasonForExistingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/2/seasons/100"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Season does not exist.");


            }
        });

    }

    @Test
    public void getSeasonWithNegativeIndex() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/2/seasons/-1"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Season does not exist.");

            }
        });

    }


}
