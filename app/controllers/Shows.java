package controllers;

import models.JsonViews.ShowWithoutSeasonsNetworkActors;
import models.Show;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.List;

import static controllers.JsonHelper.JsonErrorMessage;
import static play.libs.Json.toJson;


public class Shows extends Controller {

    private static JsonNode removeSeasonsFromShow(List<Show> shows) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Show.class, ShowWithoutSeasonsNetworkActors.class);

        return mapper.valueToTree(shows);

    }


    public static Result getShow(Long showId) {

        Logger.debug("Get show with ID = " + showId);

        Show show = Show.find.byId(showId);

        if (show == null) {
            return ok(JsonErrorMessage("Show does not exist."));
        }

        return ok(toJson(show));

    }

    public static Result getAllShows() throws IOException {

        Logger.debug("Get all shows");

        List<Show> shows = Show.find.all();
        JsonNode jsonNode = removeSeasonsFromShow(shows);

        return ok(toJson(jsonNode));
    }

    public static Result searchShow(String title) {

        String searchTerm = "%" + title + "%";
        List<Show> shows = Show.find.where().ilike("title", searchTerm).orderBy("title desc").setMaxRows(20).findList();

        return ok(toJson(removeSeasonsFromShow(shows)));

    }


}
