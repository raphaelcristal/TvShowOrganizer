package test.routes;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import test.data.TestData;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class Test_Routes_Network {

    @Test
    public void getAllShowsForNetwork() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/networks/1/shows"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode firstShow = json.get(0);
                JsonNode secondShow = json.get(1);

                assertThat(firstShow.get("title").asText()).isEqualTo("Die Simpsons");
                assertThat(secondShow.get("title").asText()).isEqualTo("Darkwing Duck");


            }
        });

    }

    @Test
    public void searchNetwork() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/networks/search?name=Fox"));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode fox = json.get(0);

                assertThat(fox.get("name").asText()).isEqualTo("Fox");


            }
        });

    }
}
