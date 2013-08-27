package controllers;

import dataload.ImdbDataLoad;
import dataload.TvdbDataLoad;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {

        return ok(index.render());

    }

    public static Result setUp(boolean fromFilesOnly) {

        if (!Http.Context.current().request().remoteAddress().equals("127.0.0.1")) {
            return forbidden();
        }


        ImdbDataLoad imdbDataLoad = new ImdbDataLoad();
        TvdbDataLoad tvdbDataLoad = new TvdbDataLoad();
        try {
            imdbDataLoad.saveShows();
            tvdbDataLoad.saveShows();
        } catch (Exception e) {
            Logger.error(e.toString());
            return ok("An error occured. Check the logs.");
        }

        return ok("Finished.");

    }

}
