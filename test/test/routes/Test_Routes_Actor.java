package test.routes;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import test.data.TestData;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class Test_Routes_Actor {

    @Test
    public void getAllShowsForActor() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/actors/1/shows"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode firstShow = json.get(0);
                JsonNode secondShow = json.get(1);

                assertThat(firstShow.get("title").asText()).isEqualTo("Darkwing Duck");
                assertThat(secondShow.get("title").asText()).isEqualTo("Die Simpsons");


            }
        });

    }

    @Test
    public void searchActor() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/actors/search?name=Tim"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode fox = json.get(0);

                assertThat(fox.get("name").asText()).isEqualTo("Tim Smith");

            }
        });

    }
}
