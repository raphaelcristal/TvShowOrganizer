package controllers;

import dataload.DataLoad;
import dataload.parsers.AbstractShowParser;
import dataload.parsers.ImdbParser;
import dataload.provider.ImdbProvider;
import models.JsonViews.ShowWithoutSeasonsNetworkActors;
import models.Show;
import models.User;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    public static Result index() {

        String token = session().get("token");
        User user = null;

        if (token != null) {
            user = User.find.where().eq("authToken.token", token).findUnique();
        }

        boolean isLoggedIn = user != null;

        //return null if there is no user
        if (user == null) {
            return ok(index.render(isLoggedIn, "null"));
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Show.class, ShowWithoutSeasonsNetworkActors.class);

        String userJson = mapper.valueToTree(user).toString();

        return ok(index.render(isLoggedIn, userJson));

    }

    public static Result setUp() {

        if (!Http.Context.current().request().remoteAddress().equals("127.0.0.1")) {
            return forbidden();
        }

        ImdbProvider imdbProvider = new ImdbProvider(Paths.get("app/dataload/data/release-dates.list"));
        ImdbParser imdbParser = new ImdbParser(imdbProvider);
        List<AbstractShowParser> parsers = new ArrayList<>();
        parsers.add(imdbParser);
        DataLoad dataLoad = new DataLoad(parsers);

        try {
            dataLoad.importShows();
        } catch (Exception e) {
            Logger.error(e.toString());
            return ok("An error occured. Check the logs.");
        }

        return ok("Finished.");

    }
}
