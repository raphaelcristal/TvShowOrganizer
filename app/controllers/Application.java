package controllers;

import dataload.DataLoad;
import dataload.parsers.AbstractShowParser;
import dataload.parsers.ImdbParser;
import dataload.provider.ImdbProvider;
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

        return ok(index.render());

    }

    public static Result setUp(boolean fromFilesOnly) {

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
