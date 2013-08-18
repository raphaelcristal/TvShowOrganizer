package controllers;

import models.Season;
import models.Show;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static controllers.JsonHelper.JsonErrorMessage;
import static play.libs.Json.toJson;

public class Seasons extends Controller {

    public static Result getSeasonById(Long id) {

        Logger.debug("Get season with id " + id);

        Season season = Season.find.byId(id);
        return ok(toJson(season));

    }

    public static Result getSeasonForShow(Long showId, int seasonNumber) {

        Logger.debug("Get season with number " + seasonNumber + " for show with ID " + showId + "");

        Show show = Show.find.byId(showId);

        if (show == null) {
            return ok(JsonErrorMessage("Show does not exist."));
        }

        Season season;
        try {
            season = show.getSeasons().get(seasonNumber - 1);
        } catch (IndexOutOfBoundsException e) {
            return ok(JsonErrorMessage("Season does not exist."));
        }

        return ok(toJson(season));

    }

    public static Result getAllSeasonsForShow(Long showId) {

        Logger.debug("Get seasons for show with ID " + showId + "");

        Show show = Show.find.byId(showId);
        if (show == null) {
            return ok(JsonErrorMessage("Show does not exist."));
        }

        List<Season> seasons = show.getSeasons();
        return ok(toJson(seasons));

    }

}
