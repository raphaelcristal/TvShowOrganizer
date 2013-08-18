package test.routes;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import test.data.TestData;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class Test_Routes_Episode {

    @Test
    public void getEpisodeByNumber() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/1/seasons/2/episodes/4"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("id").asLong()).isEqualTo(8L);
                assertThat(json.get("title").asText()).isEqualTo("Frische Fische mit drei Augen");
                assertThat(json.get("number").asInt()).isEqualTo(4);

            }
        });

    }

    @Test
    public void getEpisodeByNumberForMissingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/100/seasons/2/episodes/4"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Show does not exist.");

            }
        });

    }

    @Test
    public void getEpisodeByNumberForMissingSeason() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/1/seasons/100/episodes/4"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Season does not exist.");

            }
        });

    }

    @Test
    public void getEpisodeById() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/episodes/10"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("id").asLong()).isEqualTo(10L);
                assertThat(json.get("title").asText()).isEqualTo("Das Kavallerie-Oberkommando");
                assertThat(json.get("number").asInt()).isEqualTo(2);

            }
        });

    }

    @Test
    public void getMissingEpisodeById() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/episodes/100"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Episode does not exist.");

            }
        });

    }

    @Test
    public void getAllEpisodesForSeason() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/2/seasons/1/episodes"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.size()).isEqualTo(8);

            }
        });

    }

    @Test
    public void getAllEpisodesForMissingSeason() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/2/seasons/5/episodes"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Season does not exist.");

            }
        });

    }

    @Test
    public void getAllEpisodesForMissingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/100/seasons/1/episodes"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Show does not exist.");

            }
        });

    }

    @Test
    public void getAllEpisodesForSeasonWithNoEpisodes() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/shows/4/seasons/1/episodes"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.size()).isEqualTo(0);

            }
        });

    }

}
