package controllers;

import models.AuthToken;
import models.User;
import play.mvc.Http;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

public class Security {

    private static SecureRandom random = new SecureRandom();

    private static int TOKEN_DAYS_VALID = 7;

    public static String generateToken() {

        return new BigInteger(130, random).toString(32);

    }

    public static boolean isAuthorized(Http.Session session, User user) {

        if (session.get("token") == null || user == null) {
            return false;
        }

        AuthToken authToken = AuthToken
                .find
                .where().
                        eq("user_id", user.getId()).
                        eq("token", session.get("token"))
                .findUnique();

        if (authToken != null) {

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -TOKEN_DAYS_VALID);
            Date validFrom = calendar.getTime();

            if (authToken.getCreationDate().before(validFrom)) {
                authToken.delete();
                session.clear();
                return false;
            } else {
                return true;
            }

        }

        return authToken != null;
    }

}
