package controllers;

import models.Actor;
import models.JsonViews.ShowWithoutSeasonsNetworkActors;
import models.Show;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Set;

import static controllers.JsonHelper.JsonErrorMessage;
import static play.libs.Json.toJson;

public class Actors extends Controller {

    public static Result getAllShowsForActor(Long actorId) {

        Logger.debug("Get actor with id: " + actorId);

        Set<Show> shows = Show.find.where().eq("actors.id", actorId).findSet();

        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Show.class, ShowWithoutSeasonsNetworkActors.class);

        return ok(toJson(mapper.valueToTree(shows)));

    }

    public static Result getActorById(Long actorId) {

        Logger.debug("Get actor with id: " + actorId);

        Actor actor = Actor.find.byId(actorId);

        if (actor == null) {
            return notFound(JsonErrorMessage("Actor does not exist."));
        }

        return ok(toJson(actor));
    }

    public static Result searchActor(String name) {

        Logger.debug("Search actor with name: " + name);

        String searchTerm = "%" + name + "%";

        Set<Actor> actors = Actor.find.where().ilike("name", searchTerm).findSet();

        return ok(toJson(actors));

    }

}
