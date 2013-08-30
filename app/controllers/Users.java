package controllers;

import com.avaje.ebean.ValidationException;
import models.*;
import models.JsonViews.ShowWithoutSeasonsNetworkActors;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.PersistenceException;
import java.util.*;

import static controllers.JsonHelper.JsonErrorMessage;
import static controllers.Security.generateToken;
import static controllers.Security.isAuthorized;
import static play.libs.Json.toJson;

public class Users extends Controller {


    private static JsonNode removeSeasonsFromShow(Show shows) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Show.class, ShowWithoutSeasonsNetworkActors.class);

        return mapper.valueToTree(shows);

    }


    public static Result getShows(Long userId) {

        Logger.debug("Get shows for user with ID = " + userId);

        User user = User.find.byId(userId);
        if (user == null) {
            return ok(JsonErrorMessage("User does not exist"));
        }

        if (!isAuthorized(session(), user)) {
            return unauthorized(JsonErrorMessage("Not authorized."));
        }
        Set<Show> shows = user.getShows();

        return ok(toJson(shows));
    }

    public static Result createUser(String email, String password) {

        if (password.length() < 5) {
            return ok(JsonErrorMessage("Password length has to be at least 5."));
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        AuthToken authToken = new AuthToken();
        authToken.setCreationDate(new Date());
        authToken.setToken(generateToken());

        user.setAuthToken(authToken);

        try {

            user.save();
            Logger.debug("Successfully created new user: Email = " + email);

        } catch (ValidationException ex) {

            return badRequest(JsonErrorMessage("Invalid Email."));

        } catch (PersistenceException ex) {

            return ok(JsonErrorMessage("User already exists."));

        }

        session("token", authToken.getToken());
        return created(toJson(user));

    }

    public static Result authenticateUser(String email, String password) {

        User user = User.find.where().eq("email", email).findUnique();
        if (user == null) {
            return ok(JsonErrorMessage("User does not exist."));
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            return unauthorized(JsonErrorMessage("Wrong password."));
        }

        AuthToken authToken = user.getAuthToken();

        if (!authToken.isActive()) {
            return unauthorized(JsonErrorMessage("Your token has been revoked."));
        }

        session("token", authToken.getToken());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("token", authToken.getToken());
        objectNode.putAll((ObjectNode) toJson(user));

        return ok(toJson(objectNode));

    }

    public static Result updatePassword(Long userId, String oldPassword, String newPassword) {

        Logger.debug("Updating password for user with ID = " + userId);

        User user = User.find.byId(userId);
        if (user == null) {
            return ok(JsonErrorMessage("User does not exist"));
        }

        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return unauthorized(JsonErrorMessage("Wrong password."));
        }

        if (newPassword.length() < 5) {
            return ok(JsonErrorMessage("Password length has to be at least 5."));
        }

        user.setPassword(newPassword);
        user.save();

        return ok(toJson(user));

    }

    public static Result subscribeUserToShow(Long userId, Long showId) {

        Logger.debug("Subscribing user with id: " + userId + " to show with id: " + showId);

        User user = User.find.byId(userId);
        if (user == null) {
            return ok(JsonErrorMessage("User does not exist."));
        }

        if (!isAuthorized(session(), user)) {
            return unauthorized(JsonErrorMessage("Not authorized."));
        }

        Show show = Show.find.byId(showId);
        if (show == null) {
            return ok(JsonErrorMessage("Show does not exist."));
        }

        user.getShows().add(show);
        user.save();

        return ok(toJson(removeSeasonsFromShow(show)));

    }

    public static Result unsubscribeUserFromShow(Long userId, Long showId) {

        Logger.debug("Unsubscribing user with id: " + userId + " to show with id: " + showId);

        User user = User.find.byId(userId);
        if (user == null) {
            return ok(JsonErrorMessage("User does not exist."));
        }

        if (!isAuthorized(session(), user)) {
            return unauthorized(JsonErrorMessage("Not authorized."));
        }

        Show show = Show.find.byId(showId);
        if (show == null) {
            return ok(JsonErrorMessage("Show does not exist."));
        }

        user.getShows().remove(show);
        user.save();

        return ok(toJson(show));

    }

    public static Result latestEpisodes(Long userId) {

        Logger.debug("Dashboard for user with id: " + userId);

        User user = User.find.byId(userId);
        if (user == null) {
            return ok(JsonErrorMessage("User does not exist."));
        }

        if (!isAuthorized(session(), user)) {
            return unauthorized(JsonErrorMessage("Not authorized."));
        }


        Set<Show> shows = User.find.byId(userId).getShows();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DATE, -7);

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Show show : shows) {

            List<Season> seasons = show.getSeasons();

            List<Long> ids = new ArrayList<>();
            for (Season season : seasons) {
                ids.add(season.getId());
            }

            Set<Episode> episodes = Episode.find.where()
                    .in("season_id", ids)
                    .gt("airtime", calendar.getTime())
                    .findSet();

            String networkName = show.getNetwork() != null ? show.getNetwork().getName() : "";
            for (Episode episode : episodes) {

                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("show", show.getTitle());
                objectNode.put("airhour", show.getAirtime());
                objectNode.put("network", networkName);
                objectNode.put("description", episode.getDescription());
                objectNode.put("number", episode.getNumber());
                objectNode.put("title", episode.getTitle());
                objectNode.put("airtime", episode.getAirtime().getTime());

                arrayNode.add(objectNode);

            }

        }

        return ok(toJson(arrayNode));

    }


}

