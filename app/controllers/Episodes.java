package controllers;

import models.Episode;
import models.Season;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static controllers.JsonHelper.JsonErrorMessage;
import static play.libs.Json.toJson;

public class Episodes extends Controller {

    private static Episode findEpisodeByNumber(List<Episode> episodes, int number) {

        for (Episode episode : episodes) {

            if (episode.getNumber() == number) {
                return episode;
            }

        }
        return null;

    }

    public static Result getEpisode(Long showId, int seasonNumber, int episodeNumber) {

        Logger.debug("Get episode number " + episodeNumber + " of season number " + seasonNumber + " of show " + showId + "");

        Season season = Season.find.where().eq("show_id", showId).eq("number", seasonNumber).findUnique();

        if (season == null) {
            return notFound(JsonErrorMessage("Episode does not exist."));
        }

        Episode episode = findEpisodeByNumber(season.getEpisodes(), episodeNumber);
        if (episode == null) {
            return notFound(JsonErrorMessage("Episode does not exist."));
        }

        return ok(toJson(episode));
    }

    public static Result getAllEpisodesForShowAndSeason(Long showId, int seasonNumber) {

        Logger.debug("Get all episodes of season number " + seasonNumber + " of show " + showId + "");

        Season season = Season.find.where().eq("show_id", showId).eq("number", seasonNumber).findUnique();

        if (season == null) {
            return notFound(JsonErrorMessage("Episodes do not exist."));
        }

        List<Episode> episodes = season.getEpisodes();

        return ok(toJson(episodes));
    }


    public static Result getEpisodeById(Long episodeId) {

        Logger.debug("Get episode " + episodeId + "");

        Episode episode = Episode.find.byId(episodeId);
        if (episode == null) {
            return notFound(JsonErrorMessage("Episode does not exist."));
        }

        return ok(toJson(episode));

    }

}
