package controllers;

import models.JsonViews.ShowWithoutSeasonsNetworkActors;
import models.Network;
import models.Show;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Set;

import static controllers.JsonHelper.JsonErrorMessage;
import static play.libs.Json.toJson;

public class Networks extends Controller {

    public static Result getAllShowsForNetwork(Long networkId) {

        Logger.debug("Get shows for network with id:" + networkId);

        Set<Show> shows = Show.find.where().eq("network.id", networkId).findSet();

        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Show.class, ShowWithoutSeasonsNetworkActors.class);

        return ok(toJson(mapper.valueToTree(shows)));
    }

    public static Result getNetworkById(Long networkId) {

        Logger.debug("Get network by id:" + networkId);

        Network network = Network.find.byId(networkId);

        if (network == null) {
            return ok(JsonErrorMessage("Network does not exist."));
        }

        return ok(toJson(network));
    }

    public static Result searchNetwork(String name) {

        Logger.debug("Searching network with name " + name);
        String searchTerm = "%" + name + "%";
        Set<Network> networks = Network.find.where().ilike("name", searchTerm).findSet();

        return ok(toJson(networks));
    }

}
