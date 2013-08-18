package controllers;

import models.Episode;
import models.Season;
import models.Show;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static controllers.JsonHelper.JsonErrorMessage;
import static play.libs.Json.toJson;

public class Episodes extends Controller {

    public static Result getEpisode(Long showId, int seasonNumber, int episodeNumber) {

        Logger.debug("Get episode number " + episodeNumber + " of season number " + seasonNumber + " of show " + showId + "");

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

        Episode episode = findEpisodeByNumber(season.getEpisodes(), episodeNumber);
        if (episode == null) {
            return ok(JsonErrorMessage("Episode does not exist."));
        }

        return ok(toJson(episode));
    }

    public static Result getAllEpisodesForShowAndSeason(Long showId, int seasonNumber) {

        Logger.debug("Get all episodes of season number " + seasonNumber + " of show " + showId + "");

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

        List<Episode> episodes = season.getEpisodes();

        return ok(toJson(episodes));
    }


    public static Result getEpisodeById(Long episodeId) {

        Logger.debug("Get episode " + episodeId + "");

        Episode episode = Episode.find.byId(episodeId);
        if (episode == null) {
            return ok(JsonErrorMessage("Episode does not exist."));
        }

        return ok(toJson(episode));

    }

}
