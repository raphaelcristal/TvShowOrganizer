package controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dataload.DataLoad;
import dataload.parsers.TvdbParser;
import dataload.provider.TvdbProvider;
import models.JsonViews.ShowWithoutSeasonsNetworkActors;
import models.Show;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.Play;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static controllers.JsonHelper.JsonErrorMessage;
import static play.libs.F.Function;
import static play.libs.F.Promise;
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
            return notFound(JsonErrorMessage("Show does not exist."));
        }

        return ok(toJson(show));

    }

    public static Result searchShow(String title) {

        String searchTerm = "%" + title + "%";
        List<Show> shows = Show.find.where().ilike("title", searchTerm).orderBy("title desc").setMaxRows(20).findList();

        return ok(toJson(removeSeasonsFromShow(shows)));

    }

    public static Result searchShowOnTvdb(String title) {

        TvdbProvider tvdbProvider = new TvdbProvider(Play.application().configuration().getString("tvdb.token"));
        Promise<Result> searchResult = tvdbProvider.searchShowAsync(title).map(
                new Function<WS.Response, Result>() {
                    public Result apply(WS.Response response) throws IOException {
                        String body = response.getBody();
                        XmlMapper xmlMapper = new XmlMapper();
                        List list = xmlMapper.readValue(body, List.class);
                        return ok(toJson(list));
                    }
                }
        );

        return async(searchResult);

    }


    public static Result addShow(int tvdbId) {

        List<Object> ids = Show.find.where().eq("tvdbId", tvdbId).findIds();

        if (ids.size() > 0) {
            return status(409, JsonErrorMessage("Show already exists."));
        }

        TvdbProvider tvdbProvider = new TvdbProvider(Play.application().configuration().getString("tvdb.token"));
        TvdbParser tvdbParser = new TvdbParser(tvdbProvider, false);
        Promise<Show> showPromise = tvdbParser.parseShowAsync(tvdbId);

        Promise<Result> result = showPromise.map(new Function<Show, Result>() {
            public Result apply(Show show) {
                try {
                    List<Show> shows = new ArrayList<>();
                    shows.add(show);
                    new DataLoad().importShows(shows);
                } catch (Exception e) {
                    Logger.error("Error while importing new show", e);
                    return ok(JsonErrorMessage("An error occured."));
                }
                return ok(toJson(show));
            }
        });

        return async(result);
    }
}
