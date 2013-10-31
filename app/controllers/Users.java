package controllers;

import com.avaje.ebean.ValidationException;
import models.AuthToken;
import models.JsonViews.ShowWithoutSeasonsNetworkActors;
import models.Settings;
import models.Show;
import models.User;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

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

    public static Result latestEpisodes(Long userId, int days) {

        Logger.debug("Dashboard for user with id: " + userId);

        User user = User.find.byId(userId);
        if (user == null) {
            return ok(JsonErrorMessage("User does not exist."));
        }

        if (!isAuthorized(session(), user)) {
            return unauthorized(JsonErrorMessage("Not authorized."));
        }

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        String sql = "SELECT"
                + " sh.title as showTitle, sh.airtime as airhour,"
                + " ep.description as description, ep.number as number,"
                + " ep.title as title, ep.airtime as airtime, ne.name as network "
                + " FROM users u"
                + " JOIN users_shows us ON us.users_id = u.id"
                + " JOIN shows sh ON sh.id = us.shows_id"
                + " JOIN seasons se ON se.show_id = sh.id"
                + " JOIN episodes ep ON ep.season_id = se.id"
                + " JOIN networks ne ON ne.id = sh.network_id"
                + " WHERE u.id = ?"
                + " AND ep.airtime > ?";


        try (Connection connection = DB.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            preparedStatement.setDate(2, new java.sql.Date(calendar.getTime().getTime()));
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {

                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("show", resultSet.getString("showTitle"));
                if (resultSet.getString("airhour") != null) {
                    objectNode.put("airhour", resultSet.getString("airhour"));
                }
                objectNode.put("network", resultSet.getString("network"));
                objectNode.put("description", resultSet.getString("description"));
                objectNode.put("number", resultSet.getInt("number"));
                objectNode.put("title", resultSet.getString("title"));
                if (resultSet.getString("airtime") != null) {
                    objectNode.put("airtime", resultSet.getDate("airtime").getTime());
                }

                arrayNode.add(objectNode);

            }

            connection.close();
        } catch (SQLException e) {
            Logger.error("An error occured while fetching the dashboard for user " + userId, e);
            return internalServerError(JsonErrorMessage("An error occurred. Please contact your administrator."));
        }


        return ok(toJson(arrayNode));

    }

    public static Result updateHideDescriptions(Long userId, boolean hideShowDescriptions) {

        User user = User.find.byId(userId);

        if (user == null) {
            return ok(JsonErrorMessage("User does not exist."));
        }

        Settings settings = user.getSettings();
        settings.setHideDescriptions(hideShowDescriptions);

        return ok(toJson(settings));
    }

    public static Result updatePassedDaysToShow(Long userId, int days) {

        User user = User.find.byId(userId);

        if (user == null) {
            return ok(JsonErrorMessage("User does not exist."));
        }

        Settings settings = user.getSettings();
        settings.setPassedDaysToShow(days);

        return ok(toJson(settings));
    }


}

